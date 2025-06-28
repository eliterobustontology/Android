package com.elite.ashshakurcharity;
import android.app.Activity;
import android.webkit.WebView;
public class BackPressHandler {
    public static void handleBackPressed(Activity activity, WebView webView) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            activity.finish();
        }
    }
}