package com.elite.ashshakurcharity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.WebView;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class CameraHelper {
    private static Uri photoUri;
    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(intent, MainActivity.CAMERA_CAPTURE_REQUEST_CODE);
            }
        }
    }
    public static void openVideoCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        activity.startActivityForResult(intent, MainActivity.CAMERA_CAPTURE_REQUEST_CODE);
    }
    public static void handleCameraResult(Activity activity, WebView webView, int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.CAMERA_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri contentUri = null;
            if (data != null && data.getData() != null) {
                contentUri = data.getData();
            } else if (photoUri != null) {
                contentUri = photoUri;
            }

            if (contentUri != null) {
                String jsCode = "javascript:onCameraCaptured('" + contentUri.toString() + "')";
                webView.evaluateJavascript(jsCode, null);
            } else {
                String jsCode = "javascript:onCameraCaptured('NO_URI_AVAILABLE')";
                webView.evaluateJavascript(jsCode, null);
            }
        }
    }
    private static File createImageFile(Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, 
                ".jpg",        
                storageDir     
        );
        return image;
    }
}