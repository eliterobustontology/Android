package com.elite.qel_medistore;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private WebAppInterface webAppInterface;
    private FileChooserHelper fileChooserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable drawing system bar backgrounds
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        webView = findViewById(R.id.webView);

        // Enable JavaScript in WebView
        JavaScriptEnabler.enable(webView);

        // Initialize WebAppInterface
        webAppInterface = new WebAppInterface(this, webView);
        webView.addJavascriptInterface(webAppInterface, "Android");

        // Setup WebView client
        WebViewClientSetup.setClient(webView);
        WebViewLoader.loadFromAssets(webView, "index.html");

        // Initialize file chooser helper
        fileChooserHelper = new FileChooserHelper(this, this);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                fileChooserHelper.openFileChooser(webView, filePathCallback);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileChooserHelper.onFileChooserResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (WebViewBackHandler.canGoBack(webView)) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
