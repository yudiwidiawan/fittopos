package com.odoo.core.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.MenuAwalActivity;
import com.odoo.OdooActivity;
import com.odoo.POSWebViewActivity;
import com.odoo.R;
import com.odoo.Test;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.config.FirstLaunchConfig;
import com.odoo.core.auth.OdooAccountManager;
import com.odoo.core.auth.OdooAuthenticator;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.handler.OdooVersionException;
import com.odoo.core.rpc.listeners.IDatabaseListListener;
import com.odoo.core.rpc.listeners.IOdooConnectionListener;
import com.odoo.core.rpc.listeners.IOdooLoginCallback;
import com.odoo.core.rpc.listeners.OdooError;
import com.odoo.core.support.OUser;
import com.odoo.core.support.OdooUserLoginSelectorDialog;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OResource;
import com.odoo.datas.OConstants;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class OdooLogin extends AppCompatActivity implements View.OnClickListener,
        OdooUserLoginSelectorDialog.IUserLoginSelectListener,
        IOdooConnectionListener, IOdooLoginCallback {

    private EditText edtUsername, edtPassword, edtSelfHosted;
    private Boolean mCreateAccountRequest = false;
    private Boolean mSelfHostedURL = true;
    private Boolean mConnectedToServer = false;
    private Boolean mAutoLogin = false;
    private Boolean mRequestedForAccount = false;
    private AccountCreator accountCreator = null;
    private Spinner databaseSpinner = null;
    private List<String> databases = new ArrayList<>();
    private TextView mLoginProcessStatus = null;
    private App mApp;
    private Odoo mOdoo;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;
    private String database, url, username,host;
    private OUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login);
        System.setProperty("http.keepAlive", "false");
        sharedPref = getApplicationContext().getSharedPreferences("HOST_AND_DB", 0);
        url = sharedPref.getString("url", NULL);
        database = sharedPref.getString("db", NULL);
        if(url == "SAFE_PARCELABLE_NULL_STRING") {
            removeAccount();
        }

        mApp = (App) getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(OdooAuthenticator.KEY_NEW_ACCOUNT_REQUEST))
                mCreateAccountRequest = true;
            if (extras.containsKey(OdooActivity.KEY_ACCOUNT_REQUEST)) {
                mRequestedForAccount = true;
                setResult(RESULT_CANCELED);
            }
        }
        if (!mCreateAccountRequest) {
            if (checkIsLoggedIn()) {
                startOdooActivity();
                return;
            }
        }
        init();
    }

    private void init() {
        mLoginProcessStatus = (TextView) findViewById(R.id.login_process_status);
        //TextView mTermsCondition = (TextView) findViewById(R.id.termsCondition);
        //mTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        findViewById(R.id.btnSetURLAndDB).setOnClickListener(this);
        findViewById(R.id.btnSetURL).setOnClickListener(this);
        //findViewById(R.id.create_account).setOnClickListener(this);
        //findViewById(R.id.txvAddSelfHosted).setOnClickListener(this);
        edtSelfHosted = (EditText) findViewById(R.id.edtSelfHostedURL);

        if(url != "SAFE_PARCELABLE_NULL_STRING") {
            findViewById(R.id.layoutSelfHosted).setVisibility(View.GONE);
            findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
            findViewById(R.id.btnSetURLAndDB).setVisibility(View.GONE);
            findViewById(R.id.btnSetURL).setVisibility(View.GONE);
            findViewById(R.id.tvURL).setVisibility(View.GONE);
            findViewById(R.id.layoutUsername).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutPassword).setVisibility(View.VISIBLE);
            findViewById(R.id.tvLupaPassword).setVisibility(View.VISIBLE);
            findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
        } else {
            removeAccount();
        }
    }

    public boolean checkIsLoggedIn() {
        AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);

        // loop through all accounts to remove them
        Account[] accounts = accountManager.getAccounts();
        for (int index = 0; index < accounts.length; index++) {
            if (accounts[index].type.intern() == "com.odoo.auth") {
                return true;
            }
        }
        return false;
    }

    public void removeAccount() {
        AccountManager accountManager = (AccountManager) getApplicationContext().getSystemService(ACCOUNT_SERVICE);

        // loop through all accounts to remove them
        Account[] accounts = accountManager.getAccounts();
        for (int index = 0; index < accounts.length; index++) {
            if (accounts[index].type.intern() == "com.odoo.auth") {
                accountManager.removeAccount(accounts[index], null, null);
                Intent intent = new Intent(getApplicationContext(), OdooLogin.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }

    private void startOdooActivity() {
        url = sharedPref.getString("url", NULL);
        if(url != "SAFE_PARCELABLE_NULL_STRING") {
            startActivity(new Intent(this, MenuAwalActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.txvAddSelfHosted:
//                toggleSelfHostedURL();
//                break;
            case R.id.btnLogin:
                loginUser();
                break;
            case R.id.forgot_password:
                IntentUtils.openURLInBrowser(this, OConstants.URL_ODOO_RESET_PASSWORD);
                break;
//            case R.id.create_account:
//                IntentUtils.openURLInBrowser(this, OConstants.URL_ODOO_SIGN_UP);
//                break;
            case R.id.btnSetURL:
                showSpinnerDatabase();
                break;
            case R.id.btnSetURLAndDB:
                if(databases.size() > 0) {
                    edit = sharedPref.edit();
                    edit.putString("url", edtSelfHosted.getText().toString());
                    database = databaseSpinner.getSelectedItem().toString();
                    edit.putString("db", database);
                    edit.commit();
                    findViewById(R.id.layoutSelfHosted).setVisibility(View.GONE);
                    findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
                    findViewById(R.id.btnSetURLAndDB).setVisibility(View.GONE);
                    findViewById(R.id.layoutUsername).setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutPassword).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvLupaPassword).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
                }

                break;
        }
    }

   /* private void toggleSelfHostedURL() {
        //TextView txvAddSelfHosted = (TextView) findViewById(R.id.txvAddSelfHosted);
        if (!mSelfHostedURL) {
            mSelfHostedURL = true;
            findViewById(R.id.layoutSelfHosted).setVisibility(View.VISIBLE);
            edtSelfHosted.setOnFocusChangeListener(this);
            edtSelfHosted.requestFocus();
          //  txvAddSelfHosted.setText(R.string.label_login_with_odoo);
        } else {
            findViewById(R.id.layoutBorderDB).setVisibility(View.GONE);
            findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
            findViewById(R.id.layoutSelfHosted).setVisibility(View.GONE);
            mSelfHostedURL = false;
           // txvAddSelfHosted.setText(R.string.label_add_self_hosted_url);
            //edtSelfHosted.setText("");
        }
    }*/

   public void showSpinnerDatabase() {
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
                   if (!TextUtils.isEmpty(edtSelfHosted.getText())
                           && validateURL(edtSelfHosted.getText().toString())) {
                       edtSelfHosted.setError(null);
                       if (mAutoLogin) {
                           findViewById(R.id.controls).setVisibility(View.GONE);
                           findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
                           mLoginProcessStatus.setText(OResource.string(OdooLogin.this,
                                   R.string.status_connecting_to_server));
                       }
                       findViewById(R.id.imgValidURL).setVisibility(View.GONE);
                       findViewById(R.id.serverURLCheckProgress).setVisibility(View.VISIBLE);
                       findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
                       String test_url = createServerURL(edtSelfHosted.getText().toString());
                       Log.v("", "Testing URL :" + test_url);
                       try {
                           Odoo.createInstance(OdooLogin.this, test_url).setOnConnect(OdooLogin.this);
                       } catch (OdooVersionException e) {
                           e.printStackTrace();
                       }
                   }
           }
       }, 500);
   }

    private boolean validateURL(String url) {
        return (url.contains("."));
    }

    private String createServerURL(String server_url) {
        StringBuilder serverURL = new StringBuilder();
        if (!server_url.contains("http://") && !server_url.contains("https://")) {
            serverURL.append("http://");
        }
        serverURL.append(server_url);
        return serverURL.toString();
    }

    // User Login
    private void loginUser() {
        Log.v("", "LoginUser()");
        String serverURL = createServerURL((mSelfHostedURL) ? url :
                OConstants.URL_ODOO);
        //String databaseName;
        edtUsername = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        if (mSelfHostedURL) {
            edtSelfHosted.setError(null);
            if(url==null) {
                if (TextUtils.isEmpty(edtSelfHosted.getText())) {
                    edtSelfHosted.setError(OResource.string(this, R.string.error_provide_server_url));
                    edtSelfHosted.requestFocus();
                    return;
                }
                if (databaseSpinner != null && databases.size() > 1 && databaseSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, OResource.string(this, R.string.label_select_database), Toast.LENGTH_LONG).show();
                    findViewById(R.id.controls).setVisibility(View.VISIBLE);
                    findViewById(R.id.login_progress).setVisibility(View.GONE);
                    return;
                }
            } else {
                edtSelfHosted.setText(url);
            }


        }
        edtUsername.setError(null);
        edtPassword.setError(null);
        if (TextUtils.isEmpty(edtUsername.getText())) {
            edtUsername.setError(OResource.string(this, R.string.error_provide_username));
            edtUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtPassword.getText())) {
            edtPassword.setError(OResource.string(this, R.string.error_provide_password));
            edtPassword.requestFocus();
            return;
        }
        findViewById(R.id.controls).setVisibility(View.GONE);
        findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this,
                R.string.status_connecting_to_server));
        if (mConnectedToServer) {
            /*databaseName = databases.get(0);
            if (databaseSpinner != null) {
                databaseName = databases.get(databaseSpinner.getSelectedItemPosition());
            }*/
            mAutoLogin = false;
            loginProcess(database);
        } else {
            mAutoLogin = true;
            try {
                Odoo.createInstance(OdooLogin.this, serverURL).setOnConnect(OdooLogin.this);
            } catch (OdooVersionException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatabases() {
        if (databases.size() > 0) {
            findViewById(R.id.layoutDatabase).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSetURL).setVisibility(View.GONE);
            findViewById(R.id.tvURL).setVisibility(View.GONE);
            findViewById(R.id.btnSetURLAndDB).setVisibility(View.VISIBLE);
            databaseSpinner = (Spinner) findViewById(R.id.spinnerDatabaseList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, databases);
            databaseSpinner.setAdapter(adapter);
            databaseSpinner.setEnabled(false);
        } else {
            databaseSpinner = null;
            findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
        }
    }

    @Override
    public void onUserSelected(OUser user) {
        OdooAccountManager.login(this, user.getAndroidName());
        startOdooActivity();
    }

    @Override
    public void onRequestAccountSelect() {
        OdooUserLoginSelectorDialog dialog = new OdooUserLoginSelectorDialog(this);
        dialog.setUserLoginSelectListener(this);
        dialog.show();
    }

    @Override
    public void onNewAccountRequest() {
        init();
    }


    @Override
    public void onConnect(Odoo odoo) {
        Log.v("Odoo", "Connected to server.");
        mOdoo = odoo;
        databases.clear();
        findViewById(R.id.serverURLCheckProgress).setVisibility(View.GONE);
        edtSelfHosted.setError(null);
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_connected_to_server));
        mOdoo.getDatabaseList(new IDatabaseListListener() {
            @Override
            public void onDatabasesLoad(List<String> strings) {
                databases.addAll(strings);
                showDatabases();
                mConnectedToServer = true;
                findViewById(R.id.imgValidURL).setVisibility(View.VISIBLE);
                if (mAutoLogin) {
                    loginUser();
                }
            }
        });
    }

    @Override
    public void onError(OdooError error) {
        // Some error occurred
        if (error.getResponseCode() == Odoo.ErrorCode.InvalidURL.get() ||
                error.getResponseCode() == -1) {
            findViewById(R.id.controls).setVisibility(View.VISIBLE);
            findViewById(R.id.login_progress).setVisibility(View.GONE);
            edtSelfHosted.setError(OResource.string(OdooLogin.this, R.string.error_invalid_odoo_url));
            edtSelfHosted.requestFocus();
        }
        findViewById(R.id.controls).setVisibility(View.VISIBLE);
        findViewById(R.id.login_progress).setVisibility(View.GONE);
        findViewById(R.id.serverURLCheckProgress).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelSelect() {
    }

    private void loginProcess(String database) {
        Log.v("", "LoginProcess");
        final String username = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();
        Log.v("", "Processing Self Hosted Server Login");
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_logging_in));
        mOdoo.authenticate(username, password, database, this);
    }

    @Override
    public void onLoginSuccess(Odoo odoo, OUser user) {
        mApp.setOdoo(odoo, user);
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_login_success));
        mOdoo = odoo;
        if (accountCreator != null) {
            accountCreator.cancel(true);
        }
        accountCreator = new AccountCreator();
        accountCreator.execute(user);
    }

    @Override
    public void onLoginFail(OdooError error) {
        loginFail(error);
    }

    private void loginFail(OdooError error) {
        Log.e("Login Failed", error.getMessage());
        findViewById(R.id.controls).setVisibility(View.VISIBLE);
        findViewById(R.id.login_progress).setVisibility(View.GONE);
        edtUsername.setError(OResource.string(this, R.string.error_invalid_username_or_password));
    }

    private class AccountCreator extends AsyncTask<OUser, Void, Boolean> {

        private OUser mUser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_creating_account));
        }

        @Override
        protected Boolean doInBackground(OUser... params) {
            mUser = params[0];
            if (OdooAccountManager.createAccount(OdooLogin.this, mUser)) {
                mUser = OdooAccountManager.getDetails(OdooLogin.this, mUser.getAndroidName());
                OdooAccountManager.login(OdooLogin.this, mUser.getAndroidName());
                FirstLaunchConfig.onFirstLaunch(OdooLogin.this, mUser);
                try {
                    // Syncing company details
                    ODataRow company_details = new ODataRow();
                    company_details.put("id", mUser.getCompanyId());
                    ResCompany company = new ResCompany(OdooLogin.this, mUser);
                    company.quickCreateRecord(company_details);
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_redirecting));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startOdooActivity();
                }
            }, 200);
        }
    }
}