package com.elite.qel_medistore;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;
public class MainActivity extends AppCompatActivity {
    private WebView webView;
    public static final int FILE_PICKER_REQUEST_CODE = 1001;
    public static final int CAMERA_CAPTURE_REQUEST_CODE = 1002;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2001;
    public static final int MICROPHONE_PERMISSION_REQUEST_CODE = 3001;
    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 4001;
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 5001;
    private boolean isVideoCapture = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUIHelper.disableFullScreen(this);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        WebViewZoomHelper.disableZoom(webView);
        WebViewLoadingHandler.setLoadingHandler(webView);
        WebViewHelper.setupWebView(webView, this);
        WebViewFileAccess.enableFileAccess(webView);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebChromeClient(new DialogHelper(this));
        requestNotificationPermission();
        int batteryPercent = BatteryHelper.getBatteryPercentage(this);
    }
    @Override
    public void onBackPressed() {
        BackPressHandler.handleBackPressed(this, webView);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE) {
            FilePickerHelper.handleFilePickerResult(this, webView, requestCode, resultCode, data);
        } else if (requestCode == CAMERA_CAPTURE_REQUEST_CODE) {
            CameraHelper.handleCameraResult(this, webView, requestCode, resultCode, data);
        } else if (requestCode == AudioRecorderHelper.AUDIO_RECORD_REQUEST_CODE) {
            AudioRecorderHelper.handleAudioResult(this, webView, requestCode, resultCode, data);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isVideoCapture) {
                    CameraHelper.openVideoCamera(this);
                } else {
                    CameraHelper.openCamera(this);
                }
            } else {
                ToastHelper.showToast(this, "Camera permission denied.");
            }
        } else if (requestCode == MICROPHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AudioRecorderHelper.openAudioRecorder(this);
            } else {
                ToastHelper.showToast(this, "Microphone permission denied.");
            }
        } else if (requestCode == ContactsHelper.CONTACTS_PERMISSION_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                String contactsJson = ContactsHelper.getAllContactsJson(this);
                webView.evaluateJavascript("javascript:onContactsFetched(" + JSONObject.quote(contactsJson) + ")", null);
            } else {
                ToastHelper.showToast(this, "Contacts permission denied.");
            }
        }
    }
    public void requestCameraPermission(boolean forVideo) {
        this.isVideoCapture = forVideo;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            if (forVideo) {
                CameraHelper.openVideoCamera(this);
            } else {
                CameraHelper.openCamera(this);
            }
        }
    }
    public void requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_REQUEST_CODE);
        } else {
            AudioRecorderHelper.openAudioRecorder(this);
        }
    }
    public void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
    public void runUSSDCode(String code) {
        USSDHelper.runUSSD(this, code);
    }
    public void reloadApp() {
        AppReloadHelper.reloadApp(this);
    }
    public WebView getWebView() {
        return webView;
    }
    public void openTargetAppOrWeb(String packageName, String fallbackUrl) {
        AppLauncherHelper.openAppOrWebsite(this, packageName, fallbackUrl, webView);
    }
}