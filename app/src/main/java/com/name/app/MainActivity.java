package com.elite.qel_medistore;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;

    private static final int REQUEST_CONTACTS = 2001;
    private static final int REQUEST_NOTIFICATIONS = 2002;
    private static final int REQUEST_STORAGE = 2003;
    private static final int FILE_REQUEST = 2004;

    private boolean pendingContactsRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.WebView);
        webView.setVisibility(WebView.GONE);

        createNotificationChannel();
        setupWebView();
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "web_channel",
                    "Web Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private boolean hasPermission(String perm) {
        return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webView.addJavascriptInterface(new WebAppInterface(), "AndroidApp");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(WebView.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url.startsWith("tel:") || url.startsWith("sms:") ||
                            url.startsWith("mailto:") || url.startsWith("geo:") ||
                            url.startsWith("whatsapp:") || url.startsWith("viber:") ||
                            url.startsWith("tg:") || url.startsWith("signal:") ||
                            url.startsWith("fb:") || url.startsWith("fb-messenger:") ||
                            url.startsWith("twitter:") || url.startsWith("instagram:") ||
                            url.startsWith("youtube:") || url.startsWith("linkedin:") ||
                            url.startsWith("pinterest:") || url.startsWith("snapchat:") ||
                            url.startsWith("skype:") || url.startsWith("zoom:") ||
                            url.startsWith("slack:") || url.startsWith("spotify:") ||
                            url.startsWith("soundcloud:") || url.startsWith("content://") ||
                            url.startsWith("file://") || url.startsWith("intent://")
                    ) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> request.grant(request.getResources()));
            }

            @Override
            public boolean onShowFileChooser(
                    WebView webView,
                    ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams
            ) {
                MainActivity.this.filePathCallback = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, FILE_REQUEST);
                } catch (ActivityNotFoundException e) {
                    MainActivity.this.filePathCallback = null;
                    return false;
                }
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(
                    String origin,
                    GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE
                );
                return;
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_file");

            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
        });
    }

    private class WebAppInterface {

        @JavascriptInterface
        public void closeApp() {
            runOnUiThread(() -> finish());
        }

        @JavascriptInterface
        public void reloadApp() {
            runOnUiThread(() -> webView.reload());
        }

        @JavascriptInterface
        public String getDeviceData(String type) {
            try {
                if (type.equals("contacts")) {
                    if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
                        pendingContactsRequest = true;
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CONTACTS
                        );
                        return "REQUESTING_PERMISSION";
                    }

                    JSONArray contacts = new JSONArray();
                    ContentResolver cr = getContentResolver();
                    Cursor cur = cr.query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, null, null, null);

                    if (cur != null && cur.moveToFirst()) {
                        do {
                            JSONObject obj = new JSONObject();
                            obj.put("name", cur.getString(cur.getColumnIndex(
                                    android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                            obj.put("phone", cur.getString(cur.getColumnIndex(
                                    android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            contacts.put(obj);
                        } while (cur.moveToNext());
                        cur.close();
                    }
                    return contacts.toString();
                }

                if (type.equals("battery")) {
                    IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = registerReceiver(null, iFilter);
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    return String.valueOf(level);
                }

                if (type.equals("device")) {
                    JSONObject info = new JSONObject();
                    info.put("model", Build.MODEL);
                    info.put("brand", Build.BRAND);
                    info.put("android", Build.VERSION.RELEASE);
                    return info.toString();
                }

            } catch (Exception e) {
                return "ERROR:" + e.getMessage();
            }

            return "INVALID_TYPE";
        }

        @JavascriptInterface
        public void notify(String title, String message, String iconUrl) {
            if (Build.VERSION.SDK_INT >= 33 &&
                    !hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {

                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATIONS
                );
                return;
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(MainActivity.this, "web_channel")
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.app_icon)
                            .setAutoCancel(true);

            if (iconUrl != null && !iconUrl.isEmpty()) {
                Bitmap bmp = getBitmapFromURL(iconUrl);
                if (bmp != null) builder.setLargeIcon(bmp);
            }

            NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

        @JavascriptInterface
        public void scheduleNotifications(String jsonData) {
            try {
                JSONArray array = new JSONArray(jsonData);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int hour = obj.getInt("hour");
                    int minute = obj.getInt("minute");
                    String title = obj.getString("title");
                    String message = obj.getString("message");
                    String icon = obj.optString("icon", "");

                    scheduleNotification(hour, minute, title, message, icon, i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void scheduleNotification(int hour, int minute, String title, String message, String iconUrl, int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("iconUrl", iconUrl);
        intent.putExtra("notif_id", id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] perms, int[] results) {
        if (requestCode == REQUEST_CONTACTS) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingContactsRequest) {
                    pendingContactsRequest = false;
                    webView.post(() ->
                            webView.evaluateJavascript(
                                    "window.onAndroidContactsReady && onAndroidContactsReady();",
                                    null
                            )
                    );
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, perms, results);
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        if (req == FILE_REQUEST && filePathCallback != null) {
            Uri[] results = null;
            if (res == RESULT_OK && data != null) {
                results = new Uri[]{data.getData()};
            }
            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
        super.onActivityResult(req, res, data);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            String iconUrl = intent.getStringExtra("iconUrl");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "web_channel")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.app_icon)
                    .setAutoCancel(true);

            if (iconUrl != null && !iconUrl.isEmpty()) {
                Bitmap bmp = getBitmapFromURL(iconUrl);
                if (bmp != null) builder.setLargeIcon(bmp);
            }

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

        private Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}