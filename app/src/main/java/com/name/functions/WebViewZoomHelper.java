package com.elite.qel_medistore;
import android.webkit.WebSettings;
import android.webkit.WebView;
public class WebViewZoomHelper {
    public static void disableZoom(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
    }
}