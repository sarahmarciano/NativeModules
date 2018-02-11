package com.nativemodules;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Random;

public class ContactsManager extends ReactContextBaseJavaModule {
    ReactApplicationContext mContext;

    public ContactsManager(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "ContactsManager";
    }

    @ReactMethod
    public void getContacts() {
        ContentResolver cr = mContext.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            ArrayList<String> alContacts = new ArrayList<String>();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String image = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        alContacts.add(name + "*" + contactNumber + "*" + image);
                        break;
                    }
                    pCur.close();
                }
            } while (cursor.moveToNext());
            PrimeThread p = new PrimeThread(mContext, alContacts);
            p.start();
        }
    }
}

class PrimeThread extends Thread {
    ReactApplicationContext mContext;
    ArrayList<String> alContacts;
    ArrayList<String> sentContacts;
    PrimeThread(ReactApplicationContext mContext, ArrayList<String> alContacts) {
        this.mContext = mContext;
        this.alContacts = alContacts;
        sentContacts = new ArrayList<String>();
    }


    public void run() {
       while (true) {
           Random r = new Random();
           int n = r.nextInt(alContacts.size());
           if (!sentContacts.contains(alContacts.get(n))) {
               mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("aaa", alContacts.get(n));
               sentContacts.add(alContacts.get(n));
               try {
                   Thread.sleep(2000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }
    }
}
