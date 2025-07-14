package com.elite.homecare;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
public class WebViewHelper {
    public static void setupWebView(WebView webView, Context context) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
    }
}