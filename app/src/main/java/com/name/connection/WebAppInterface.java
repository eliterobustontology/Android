package com.elite.ashshakurcharity;
import android.content.Context;
import android.webkit.JavascriptInterface;
public class WebAppInterface {
    Context mContext;
    public WebAppInterface(Context context) {
        this.mContext = context;
    }
    @JavascriptInterface
    public void vibrate(int milliseconds) {
        VibrationHelper.vibrate(mContext, milliseconds);
    }
    @JavascriptInterface
    public int getBatteryPercentage() {
        return BatteryHelper.getBatteryPercentage(mContext);
    }
    @JavascriptInterface
    public void showToast(String message) {
        ToastHelper.showToast(mContext, message);
    }
    @JavascriptInterface
    public void showLongToast(String message) {
        ToastHelper.showLongToast(mContext, message);
    }
    @JavascriptInterface
    public void openFilePicker() {
        if (mContext instanceof MainActivity) {
            FilePickerHelper.openFilePicker((MainActivity) mContext);
        }
    }
    @JavascriptInterface
    public void openCamera() {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).requestCameraPermission(false);
        }
    }
    @JavascriptInterface
    public void openVideoCamera() {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).requestCameraPermission(true);
        }
    }
    @JavascriptInterface
    public void openMicrophone() {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).requestMicrophonePermission();
        }
    }
    @JavascriptInterface
    public void downloadFile(String url, String filename) {
        FileDownloadHelper.downloadFile(mContext, url, filename);
    }
    @JavascriptInterface
    public void showNotification(String title, String message) {
        NotificationHelper.showNotification(mContext, title, message);
    }
    @JavascriptInterface
    public void runUSSD(String code) {
        ((MainActivity) mContext).runUSSDCode(code);
    }
    @JavascriptInterface
    public void reloadApp() {
        ((MainActivity) mContext).reloadApp();
    }
}