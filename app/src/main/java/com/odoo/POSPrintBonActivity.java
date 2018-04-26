package com.odoo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.odoo.core.support.OdooCompatActivity;

public class POSPrintBonActivity extends OdooCompatActivity {

    WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posprint_bon);
        myWebView = (WebView) findViewById(R.id.webview);
        String html = this.getIntent().getStringExtra("bonpage");
        WebView webView = new WebView(getApplicationContext());
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view);
                myWebView = null;
            }
        });

        // Generate an HTML document on the fly:
        webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        myWebView = webView;

    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView view) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

    }
}
