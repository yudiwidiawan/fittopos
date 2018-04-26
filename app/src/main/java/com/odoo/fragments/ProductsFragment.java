package com.odoo.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.odoo.ProductsWebViewActivity;
import com.odoo.R;
import com.odoo.core.support.OUser;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

/**
 * Created by makan on 14/07/2017.
 */

public class ProductsFragment extends Fragment {

    private String url, host;
    private SharedPreferences sharedPref;
    private WebView myWebView;
    private View view;


    public static ProductsFragment newInstance() {

        Bundle args = new Bundle();

        ProductsFragment fragment = new ProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getApplicationContext().getSharedPreferences("HOST_AND_DB", 0);
        host = sharedPref.getString("url", NULL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);
        Toast.makeText(getActivity(), "1" + host, Toast.LENGTH_SHORT).show();
        myWebView = (WebView) view.findViewById(R.id.wvProducts);
        myWebView.setVisibility(View.GONE);
        //Bundle b = this.getIntent().getExtras();
        WebSettings webSettings = myWebView.getSettings();

        OUser user = OUser.current(getActivity());
        final String username = user.getUsername();
        final String password = user.getPassword();
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
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Toast.makeText(getActivity(), "2" + host, Toast.LENGTH_SHORT).show();
            }

            @Override
            @SuppressLint("SetJavaScriptEnabled")
            public void onPageFinished(final WebView view, String url) {
                Toast.makeText(getActivity(), "3" + host, Toast.LENGTH_SHORT).show();
                if(!url.equals(host + "/web/login")) {
                    myWebView.setVisibility(View.VISIBLE);
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
        myWebView.loadUrl("google.co.id");
        return view;
    }
}