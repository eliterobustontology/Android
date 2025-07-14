package com.elite.homecare;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
public class SystemUIHelper {
    public static void disableFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.getWindow().setDecorFitsSystemWindows(true);
            }
        }
    }
}