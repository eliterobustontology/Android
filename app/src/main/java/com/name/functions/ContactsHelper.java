package com.elite.qel_medistore;
import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
public class ContactsHelper {
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 5001;
    public static void requestContactsPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                CONTACTS_PERMISSION_REQUEST_CODE);
        } else {
            String contactsJson = getAllContactsJson(activity);
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).runOnUiThread(() -> {
                    ((MainActivity) activity).getWebView().evaluateJavascript("javascript:onContactsFetched(" + JSONObject.quote(contactsJson) + ")", null);
                });
            }
        }
    }
    public static String getAllContactsJson(Activity activity) {
        JSONArray contactsArray = new JSONArray();
        HashMap<String, JSONObject> contactMap = new HashMap<>();
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    JSONObject contact;
                    if (contactMap.containsKey(contactId)) {
                        contact = contactMap.get(contactId);
                        contact.getJSONArray("phones").put(phoneNumber);
                    } else {
                        contact = new JSONObject();
                        contact.put("id", contactId);
                        contact.put("name", name);
                        JSONArray phones = new JSONArray();
                        phones.put(phoneNumber);
                        contact.put("phones", phones);
                        contactMap.put(contactId, contact);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        for (JSONObject c : contactMap.values()) {
            contactsArray.put(c);
        }
        return contactsArray.toString();
    }
    public static boolean addContact(Activity activity, String name, String phone) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
            .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
            .build());

        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean updateContact(Activity activity, String oldName, String newName, String newPhone) {
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI,
            new String[]{ContactsContract.Data.RAW_CONTACT_ID},
            ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + "=?",
            new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, oldName},
            null);
        if (cursor != null && cursor.moveToFirst()) {
            String rawContactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.RAW_CONTACT_ID));
            cursor.close();
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                    ContactsContract.Data.MIMETYPE + "=?",
                new String[]{rawContactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                .build());
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND " +
                    ContactsContract.Data.MIMETYPE + "=?",
                    new String[]{rawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone)
                .build());
            try {
                resolver.applyBatch(ContactsContract.AUTHORITY, ops);
                return true;
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (cursor != null) cursor.close();
        return false;
    }
    public static boolean deleteContact(Activity activity, String name) {
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + "=?",
            new String[]{name}, null);
        if (cursor != null && cursor.moveToFirst()) {
            String lookupKey = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            int deleted = resolver.delete(uri, null, null);
            cursor.close();
            return deleted > 0;
        }
        if (cursor != null) cursor.close();
        return false;
    }
}