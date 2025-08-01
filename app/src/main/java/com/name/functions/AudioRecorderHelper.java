package com.elite.qel_medistore;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
public class AudioRecorderHelper {
    public static final int AUDIO_RECORD_REQUEST_CODE = 1003;
    public static void openAudioRecorder(Activity activity) {
        Intent intent = new Intent(android.provider.MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, AUDIO_RECORD_REQUEST_CODE);
        } else {
            ToastHelper.showToast(activity, "No audio recorder app found.");
        }
    }
    public static void handleAudioResult(Activity activity, WebView webView, int requestCode, int resultCode, Intent data) {
        if (requestCode == AUDIO_RECORD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri audioUri = null;
            if (data != null && data.getData() != null) {
                audioUri = data.getData();
            }
            if (audioUri != null) {
                String uriString = audioUri.toString();
                Log.d("AudioRecorderHelper", "Recorded audio URI: " + uriString);
                String jsCode = "javascript:onAudioRecorded(" + toJsString(uriString) + ")";
                webView.evaluateJavascript(jsCode, null);
            } else {
                Log.d("AudioRecorderHelper", "No URI returned from audio recorder.");
                String jsCode = "javascript:onAudioRecorded('NO_URI_AVAILABLE')";
                webView.evaluateJavascript(jsCode, null);
            }
        }
    }
    private static String toJsString(String value) {
        return "'" + value.replace("'", "\\'") + "'";
    }
}