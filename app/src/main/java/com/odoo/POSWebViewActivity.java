package com.odoo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.core.account.OdooLogin;
import com.odoo.core.account.OdooUserObjectUpdater;
import com.odoo.core.auth.OdooAccountManager;
import com.odoo.core.support.OUser;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OResource;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class POSWebViewActivity extends OdooCompatActivity {

    private App app;
    private WebView myWebView;
    private TextView tvLoading;
    private ProgressBar pbLoading;
    private String url, host;
    private SharedPreferences sharedPref;
    private boolean First = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posweb_view);

        app = (App) getApplicationContext();
        sharedPref = getApplicationContext().getSharedPreferences("HOST_AND_DB", 0);
        tvLoading = (TextView) findViewById(R.id.txtLoading);
        pbLoading = (ProgressBar) findViewById(R.id.pbWebView);

        url = sharedPref.getString("url", NULL);
        //Log.d("URL-nya adalah : ", url);
        OUser user = OUser.current(this);

        if(url != "SAFE_PARCELABLE_NULL_STRING") {
            host = user.getHost();
        } else {
            removeAccount();
        }

        final String username = user.getUsername();
        final String password = user.getPassword();
        validateUserObject();

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setVisibility(View.GONE);
        //Bundle b = this.getIntent().getExtras();
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        myWebView.clearCache(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        final JsInterfaceLogin jsIL = new JsInterfaceLogin(getApplicationContext());
        myWebView.addJavascriptInterface(jsIL, "MY_APP");
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.equals(host + "/web?#view_type=kanban&model=pos.config&menu_id=195&action=311") ||
                        url.equals(host + "/web#action=point_of_sale.action_client_pos_menu")) {
                    myWebView.loadUrl(host + "/pos/web/#action=pos.ui");
                    finish();
                }
            }

            @Override
            @SuppressLint("SetJavaScriptEnabled")
            public void onPageFinished(final WebView view, String url) {
                if(url.equals(host + "/pos/web") || url.equals(host + "/pos/web/#action=pos.ui")
                         || url.equals(host + "/pos/web/")) {
                    myWebView.setVisibility(View.VISIBLE);
                    tvLoading.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.GONE);
                    String js = "javascript:try {" +
                            "var oriPrint = self.print;" +
                            "self.print = function() {" +
                            "var html = document.getElementsByClassName('pos-sale-ticket');" +
                            " MY_APP.print_receipt(html[0].innerText); return oriPrint();};}" +
                            "catch(err){alert('Error : ' + err);}";
                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    } else {
                        view.loadUrl(js);
                    }
               } else {
                    final String js = "javascript:try {document.forms[0].login.value='" + username + "';" +
                            "document.forms[0].password.value='" + password + "';" +
                            "document.forms[0].submit();" +
                            "}catch(err) {" +
                            "}";
                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    } else {
                        view.loadUrl(js);
                    }
                }
            }
        });
        myWebView.loadUrl(host + "/pos/web");
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

    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            finish();
        }
    }

    public class JsInterfaceLogin {

        private final Context context;
        private WebView webView;

        public JsInterfaceLogin(Context context) {
            this.context = context;
        }

        public void setWebView(WebView webView) {
            this.webView = webView;
        }

        @JavascriptInterface
        public void closePOS() {
            Toast.makeText(context, "AKAN KETUTUP!", Toast.LENGTH_SHORT).show();
            //When user logged in, you can detect it in here
        }
        @JavascriptInterface
        public void print_receipt(String html) {
            String textToPrint = html.replace("\tRp","   Rp");
            textToPrint = textToPrint.replace("Subtotal:   Rp","Subtotal:\t    Rp");
            textToPrint = textToPrint.replace("PPn   Rp","PPn\t\t    Rp");
            textToPrint = textToPrint.replace("Discount:   Rp","Discount:\t    Rp");
            textToPrint = textToPrint.replace("Cash (IDR)   Rp","Cash (IDR)\t    Rp");
            textToPrint = textToPrint.replace("Total:   Rp","Total:\t\t    Rp");
            textToPrint = textToPrint.replace("Change:   Rp","Change:\t\t    Rp");
            //textToPrint = textToPrint.replace("Rp","<right>Rp ");
            //Toast.makeText(context,textToPrint,Toast.LENGTH_LONG).show();
            try {
                Intent intent = new Intent("pe.diegoveloper.printing");
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, textToPrint);
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=pe.diegoveloper.printerserverapp"));
                startActivity(intent);
            }
        }

        @JavascriptInterface
        public void close() {
            finish();
        }


    }


    private void validateUserObject() {
        if (OdooAccountManager.anyActiveUser(this)) {
            OUser user = OUser.current(this);

            if (!OdooAccountManager.isValidUserObj(this, user)
                    && app.inNetwork()) {
                OdooUserObjectUpdater.showUpdater(this, new OdooUserObjectUpdater.OnUpdateFinish() {
                    @Override
                    public void userObjectUpdateFinished() {
                        startActivity(new Intent(POSWebViewActivity.this, OdooLogin.class));
                        finish();
                    }

                    @Override
                    public void userObjectUpdateFail() {
                        Toast.makeText(POSWebViewActivity.this, OResource.string(POSWebViewActivity.this,
                                R.string.toast_something_gone_wrong), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        }
    }
}
