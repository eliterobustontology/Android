package com.elite.qel_medistore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class USSDHelper {

    public static void runUSSD(Context context, String phoneNumber) {
        try {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                Toast.makeText(context, "Phone number is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            phoneNumber = phoneNumber.replaceAll("[^0-9+]", "");

            String uriString = "tel:" + phoneNumber;

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uriString));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to dial number", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
