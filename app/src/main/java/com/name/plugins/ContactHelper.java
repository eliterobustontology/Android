package com.elite.wallet;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactHelper {
    private Context context;
    private static ContactHelper instance;

    private ContactHelper(Context context) {
        this.context = context;
    }

    public static ContactHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ContactHelper(context);
        }
        return instance;
    }

    public String getContacts() {
        StringBuilder contacts = new StringBuilder();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.append(name).append(": ").append(phone).append("\n");
            }
            cursor.close();
        }
        return contacts.toString();
    }

    public boolean createContact(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);

        ContentResolver resolver = context.getContentResolver();
        Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        if (rawContactUri == null) return false;

        long rawContactId = Long.parseLong(rawContactUri.getLastPathSegment());
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);

        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);

        return true;
    }

    public boolean updateContact(String name, String newPhone) {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                new String[]{name},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone);

            int updated = resolver.update(
                    ContactsContract.Data.CONTENT_URI,
                    values,
                    ContactsContract.Data.CONTACT_ID + " = ? AND " +
                            ContactsContract.Data.MIMETYPE + " = ?",
                    new String[]{contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}
            );

            return updated > 0;
        }

        return false;
    }

    public boolean deleteContact(String name) {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                new String[]{name},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            cursor.close();

            int deleted = resolver.delete(
                    ContactsContract.RawContacts.CONTENT_URI,
                    ContactsContract.RawContacts.CONTACT_ID + " = ?",
                    new String[]{contactId}
            );

            return deleted > 0;
        }

        return false;
    }
}
