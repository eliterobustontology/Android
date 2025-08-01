package com.elite.qel_medistore;
import android.app.Activity;
import android.content.Intent;
public class AppReloadHelper {
    public static void reloadApp(Activity activity) {
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }
}