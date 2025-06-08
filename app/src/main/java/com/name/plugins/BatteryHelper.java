package com.elite.qel_medistore;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryHelper {
    private Context context;

    public BatteryHelper(Context context) {
        this.context = context;
    }

    public String getBatteryStatus() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            int batteryPct = (level * 100) / scale;
            String chargingState = (status == BatteryManager.BATTERY_STATUS_CHARGING) ? "Charging" : "Not Charging";

            return "Battery Level: " + batteryPct + "%, Status: " + chargingState;
        }
        return "Battery status unavailable.";
    }
}
