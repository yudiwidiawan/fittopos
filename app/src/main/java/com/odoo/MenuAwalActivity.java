package com.odoo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.odoo.adapters.ProductsAdapter;
import com.odoo.addons.products.models.Product;
import com.odoo.addons.products.models.Products;
import com.odoo.core.account.OdooLogin;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.OUser;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OStringColorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import odoo.controls.OForm;


public class MenuAwalActivity extends OdooCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPref;
    private String url, host, username, password;
    private boolean doubleBackToExitPressedOnce = false;
    private WebView wvMain;
    private ScrollView scWebView, scSetting;
    private ProgressDialog loadingWeb;
    private long downloadTaskId;
    private OdooUtility odoo;
    private List<Product> arrayListProduct;
    private RecyclerView listViewProduct;
    private ProductsAdapter productsAdapter;
    private OUser user;
    private FloatingActionButton fabProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_awal);
        listViewProduct = (RecyclerView) findViewById(R.id.lvProduct);
        fabProduct = (FloatingActionButton) findViewById(R.id.fabProduct);
        loadingWeb = new ProgressDialog(this);
        loadingWeb.setMessage("Memuat data");
        loadingWeb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingWeb.setIndeterminate(true);
        loadingWeb.setCancelable(false);
        sharedPref = getApplicationContext().getSharedPreferences("HOST_AND_DB", 0);
        user = OUser.current(this);
        OForm form = (OForm) findViewById(R.id.profileDetails);
        int color = OStringColorUtil.getStringColor(this, user.getName());
        odoo = new OdooUtility(user.getHost(), "object");
        form.setIconTintColor(color);
        ODataRow userData = new ODataRow();
        userData.put("name", user.getName());
        userData.put("user_login", user.getUsername());
        userData.put("server_url", user.getHost());
        userData.put("database", user.getDatabase());
        userData.put("version", user.getOdooVersion().getServerSerie());
        userData.put("timezone", user.getTimezone());
        form.initForm(userData);
        Bitmap avatar;
        if (user.getAvatar().equals("false")) {
            avatar = BitmapUtils.getAlphabetImage(this, user.getName());
        } else {
            avatar = BitmapUtils.getBitmapImage(this, user.getAvatar());
        }
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(avatar);
        scWebView = (ScrollView) findViewById(R.id.svWebView);
        scSetting = (ScrollView) findViewById(R.id.svSetting);

        username = user.getUsername();
        password = user.getPassword();

       wvMain = (WebView) findViewById(R.id.wvMain);
        wvMain.setVisibility(View.GONE);
        WebSettings webSettings = wvMain.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        wvMain.clearCache(true);
        wvMain.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                loadingWeb.setMessage("Memuat data : " + newProgress + "%");
            }

        });
        host = user.getHost();

        findViewById(R.id.btnDashboard).setOnClickListener(this);
        findViewById(R.id.btnPOS).setOnClickListener(this);
        findViewById(R.id.btnProducts).setOnClickListener(this);
        findViewById(R.id.btnInvoicing).setOnClickListener(this);
        findViewById(R.id.btnSettings).setOnClickListener(this);
        findViewById(R.id.btnProfileLogout).setOnClickListener(this);

        Button dashboardBtn = (Button) findViewById(R.id.btnDashboard);
        dashboardBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        wvMain.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingWeb.show();
            }

            @Override
            @SuppressLint("SetJavaScriptEnabled")
            public void onPageFinished(final WebView view, String url) {
                loadingWeb.hide();
                wvMain.setVisibility(View.VISIBLE);
                if(url.equals(host + "/pos/web") || url.equals(host + "/pos/web/#action=pos.ui")
                        || url.equals(host + "/pos/web/") || url.equals(host + "/web#view_type=form&model=board.board&menu_id=203&action=325")
                        || url.equals(host + "/web#view_type=kanban&model=product.product&menu_id=190&action=306") ||
                url.equals(host + "/web#min=1&limit=80&view_type=list&model=account.invoice&menu_id=118&action=197")) {
                    wvMain.setVisibility(View.VISIBLE);
                    String js = "javascript:try {" +
                            "var cha = document.getElementsByTagName(\"header\")[0];" +
                            "cha.remove();" +
                            "}catch(err){}";
                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    } else {
                        view.loadUrl(js);
                    }
                } else if(url.equals(host + "web#view_type=kanban&model=pos.config&menu_id=195&action=311")) {
                    changeButtonSelected(findViewById(R.id.bottomBar), R.id.btnDashboard);
                    view.loadUrl(host + "/web#view_type=form&model=board.board&menu_id=203&action=325");
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
        //wvMain.loadUrl(host + "/web#view_type=form&model=board.board&menu_id=203&action=325");

        fabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuAwalActivity.this, FormProduct.class));
            }
        });
        fillListProduct();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
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
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnDashboard:
                changeButtonSelected(findViewById(R.id.bottomBar), R.id.btnDashboard);
                findViewById(R.id.svListProduct).setVisibility(View.GONE);
                scWebView.setVisibility(View.VISIBLE);
                scSetting.setVisibility(View.GONE);
                wvMain.loadUrl(host + "/web#view_type=form&model=board.board&menu_id=203&action=325");
                break;
            case R.id.btnPOS:
                startActivity(new Intent(getApplicationContext(), POSWebViewActivity.class));
                break;
            case R.id.btnProducts:
                 scWebView.setVisibility(View.GONE);
                scSetting.setVisibility(View.GONE);
                 findViewById(R.id.svListProduct).setVisibility(View.VISIBLE);
                changeButtonSelected(findViewById(R.id.bottomBar), R.id.btnProducts);
                List conditions = Arrays.asList(
                        Arrays.asList(Arrays.asList("active", "=", "1")));

                Map fields = new HashMap() {{
                    put("fields", Arrays.asList(
                            "id",
                            "display_name",
                            "list_price",
                            "image_medium"
                    ));
                }};

                downloadTaskId = odoo.search_read(listener, user.getDatabase().toString(),
                        user.getUserId().toString(), user.getPassword().toLowerCase(),
                        "product.product", conditions, fields);
                break;
            case R.id.btnInvoicing:
                changeButtonSelected(findViewById(R.id.bottomBar), R.id.btnInvoicing);
                scWebView.setVisibility(View.VISIBLE);
                scSetting.setVisibility(View.GONE);
                findViewById(R.id.svListProduct).setVisibility(View.GONE);
                wvMain.loadUrl(host + "/web#min=1&limit=80&view_type=list&model=account.invoice&menu_id=118&action=197");
                break;
            case R.id.btnSettings:
                changeButtonSelected(findViewById(R.id.bottomBar), R.id.btnSettings);
                scWebView.setVisibility(View.GONE);
                scSetting.setVisibility(View.VISIBLE);
                findViewById(R.id.svListProduct).setVisibility(View.GONE);
                break;
            case R.id.btnProfileLogout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_confirm);
                builder.setMessage(R.string.toast_are_you_sure_logout);
                builder.setPositiveButton(R.string.label_logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                });
                builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }

    }

    XMLRPCCallback listener = new XMLRPCCallback() {
        public void onResponse(long id, Object result) {

            Looper.prepare();

            if(id == downloadTaskId) {
                Object[] classObjs = (Object[]) result;
                int length = classObjs.length;

                if (length > 0) {

                    Products products = new Products(MenuAwalActivity.this);
                    products.deleteAll();

                    for (int i = 0; i < length; i++) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> classObj = (Map<String, Object>)
                                classObjs[i];
                        Log.d("datanya", classObj.get("image_medium").toString());
                        products.setData(classObj);
                        products.addToDb();

                    }

                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          fillListProduct();
                                      }
                                  }
                    );
                } else {
                    odoo.MessageDialog(MenuAwalActivity.this,
                            "Product not found");
                }

            }
            Looper.loop();
        }

        public void onError(long id, XMLRPCException error) {
            Looper.prepare();
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(MenuAwalActivity.this, error.getMessage());
            Looper.loop();

        }
        public void onServerError(long id, XMLRPCServerException error) {
            Looper.prepare();
            Log.e("SEARCH", error.getMessage());
            odoo.MessageDialog(MenuAwalActivity.this, error.getMessage());
            Looper.loop();
        }
    };

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));
            result.addAll(viewArrayList);
        }
        return result;
    }
    public void changeButtonSelected(View v, int id) {
        ArrayList<View> allViewsWithinMyTopView = getAllChildren(v);
        for (View child : allViewsWithinMyTopView) {
            if (child instanceof Button) {
                Button childButton = (Button) child;
                if (childButton.getId() == id) {
                    child.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    child.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }

    public void fillListProduct() {
        Products products = new Products(MenuAwalActivity.this);
        arrayListProduct = products.getAllProducts();
        productsAdapter = new ProductsAdapter(arrayListProduct);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listViewProduct.setLayoutManager(mLayoutManager);
        listViewProduct.setItemAnimator(new DefaultItemAnimator());
        listViewProduct.setAdapter(productsAdapter);
        productsAdapter.notifyDataSetChanged();
    }

}
