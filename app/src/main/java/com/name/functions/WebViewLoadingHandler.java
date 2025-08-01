package com.elite.qel_medistore;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
public class WebViewLoadingHandler {
    public static void setLoadingHandler(final WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });
    }
}