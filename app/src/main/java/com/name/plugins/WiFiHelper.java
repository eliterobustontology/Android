package com.elite.wallet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import androidx.core.content.ContextCompat;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import java.util.List;

public class WiFiHelper {
    private Context context;
    private WifiManager wifiManager;

    public WiFiHelper(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean isWiFiEnabled() {
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    public void setWiFiEnabled(boolean enable) {
        if (wifiManager != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            wifiManager.setWifiEnabled(enable);
        }
    }

    public String getNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return "Connected to WiFi";
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return "Connected to Mobile Data";
                    }
                }
            } else {
                return "Network status unavailable (Android version too low).";
            }
        }
        return "No active internet connection.";
    }

    public String getAvailableNetworks() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "Permission required to scan WiFi networks!";
        }

        if (wifiManager != null) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults != null && !scanResults.isEmpty()) {
                StringBuilder wifiList = new StringBuilder();
                for (ScanResult result : scanResults) {
                    wifiList.append(result.SSID).append(" - ").append(result.capabilities).append("\n");
                }
                return wifiList.toString();
            }
        }
        return "No available WiFi networks.";
    }
}
