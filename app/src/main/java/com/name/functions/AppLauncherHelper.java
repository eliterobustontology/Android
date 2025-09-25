package com.elite.qel_medistore;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.WebView;

import java.util.List;

public class AppLauncherHelper {

    public static void openAppOrWebsite(Context context, String packageName, String fallbackUrl, WebView webView) {
        if (isAppInstalled(context, packageName)) {
            launchApp(context, packageName);
        } else {
            if (webView != null) {
                webView.loadUrl(fallbackUrl);
            }
        }
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static void launchApp(Context context, String packageName) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        }
    }
}
