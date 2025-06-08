package com.elite.wallet;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileChooserHelper {
    private Context context;
    private Activity activity;
    private ValueCallback<Uri[]> uploadMessage;
    private Uri photoURI;
    public static final int FILE_CHOOSER_REQUEST = 102;
    public static final int PERMISSION_REQUEST_CODE = 101;

    public FileChooserHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void openFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback) {
        if (uploadMessage != null) {
            uploadMessage.onReceiveValue(null);
        }
        uploadMessage = filePathCallback;

        if (!hasPermission(Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA);
            return;
        }

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoURI = createImageUri();
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        Intent selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectIntent.addCategory(Intent.CATEGORY_OPENABLE);
        selectIntent.setType("*/*");

        Intent chooserIntent = Intent.createChooser(selectIntent, "Select File");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});

        activity.startActivityForResult(chooserIntent, FILE_CHOOSER_REQUEST);
    }

    public void onFileChooserResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST) {
            if (uploadMessage == null) return;

            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                results = data == null ? new Uri[]{photoURI} : new Uri[]{data.getData()};
            }
            uploadMessage.onReceiveValue(results);
            uploadMessage = null;
        }
    }

    private Uri createImageUri() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }
}
