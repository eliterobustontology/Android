package com.elite.ashshakurcharity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
public class FilePickerHelper {
    public static void openFilePicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), MainActivity.FILE_PICKER_REQUEST_CODE);
    }
    public static void handleFilePickerResult(Activity activity, WebView webView, int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String jsCode = "javascript:onFilePicked('" + fileUri.toString() + "')";
                webView.evaluateJavascript(jsCode, null);
            }
        }
    }
}