package com.elite.qel_medistore;

import android.content.Context;
import android.os.Vibrator;

public class VibrationHelper {
    private Context context;

    public VibrationHelper(Context context) {
        this.context = context;
    }

    public void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(duration);
        }
    }
}