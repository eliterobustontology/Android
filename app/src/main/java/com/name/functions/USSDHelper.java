package com.elite.ashshakurcharity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
public class USSDHelper {
    public static void runUSSD(Context context, String ussdCode) {
        try {
            if (!ussdCode.startsWith("*")) {
                ussdCode = "*" + ussdCode;
            }
            if (!ussdCode.endsWith("#")) {
                ussdCode = ussdCode + "#";
            }
            String encodedHash = Uri.encode("#");
            String uriString = "tel:" + ussdCode.replace("#", encodedHash);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uriString)); 
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to run USSD code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}