package com.megatron.sendbum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements OnDismissListener {
    public int ENTER_NUMBER_ACTIVITY_REQUEST = 300;
    public int PICK_PHONE_RESULT = 101;
    //    public int PICK_RESULT = 100;
    public int SPLASH_ACTIVITY_RESULT = 200;
    private boolean _dialled = false;
    private boolean _notificated_recived = false;
    private int _number_starts = 1;
    private int _operatorId = -1;
    private int _operatorId2 = -1;
    private int sel_contact = -1;
    private Dialog currentDialog = null;

    private void CheckComments() {
        if (this.currentDialog != null) {
            this.currentDialog.hide();
            this.currentDialog.show();
            return;
        } else {
            if ((this._number_starts < 3) || (!this._dialled) || (this._notificated_recived))
                return;
            {
                final Dialog localDialog = new Dialog(this);
                localDialog.setContentView(R.layout.comment_dialog);
                localDialog.getWindow().setLayout(-1, -2);
                localDialog.setTitle(R.string.your_rate);
                localDialog.setCancelable(true);
                TextView localTextView1 = (TextView) localDialog.findViewById(R.id.hiperlink01);
                localTextView1.setText(Html.fromHtml(getString(R.string.send_your_rate)));
                localTextView1.setMovementMethod(LinkMovementMethod.getInstance());
                TextView localTextView2 = (TextView) localDialog.findViewById(R.id.hiperlink02);
                localTextView2.setText(Html.fromHtml(getString(R.string.send_your_mail)));
                localTextView2.setMovementMethod(LinkMovementMethod.getInstance());
                ((Button) localDialog.findViewById(R.id.Button01)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramAnonymousView) {
                        localDialog.dismiss();
                    }
                });
                ((Button) localDialog.findViewById(R.id.Button02)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramAnonymousView) {
                        MainActivity.this.startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(getString(R.string.address_program))));
                    }
                });
                SharedPreferences.Editor localEditor = getSharedPreferences("SETTINGS", 0).edit();
                localEditor.putBoolean("NOTIFICATION_RECIVED", true);
                localEditor.commit();
                this._notificated_recived = true;
                localDialog.setOnDismissListener(this);
                this.currentDialog = localDialog;
                localDialog.show();
                return;
            }
        }
    }

    private void SetOperatorText() {
        this._operatorId = getSharedPreferences("SETTINGS", 0).getInt("CURRENT_OPERATOR", -1);
        this._operatorId2 = getSharedPreferences("SETTINGS", 0).getInt("SECOND_OPERATOR", -1);
        if ((this._operatorId < 0) && (_operatorId2 < 0)) {
            return;
        }
        String text = (_operatorId > 0) ? Common.getOperatorById(this._operatorId).Name : "";
        text = text + ((!text.trim().equals("") && (_operatorId2 > 0)) ? " / " : "");
        text = text + ((_operatorId2 > 0) ? Common.getOperatorById(this._operatorId2).Name : "");
        setTitle(" " + getString(R.string.app_name) + " - " + text);
        //Common.getOperatorById(this._operatorId).Name);
        ((EditText) findViewById(R.id.editText_sendSim2)).setEnabled((_operatorId2 < 0) ? false : true);
        ((ImageButton) findViewById(R.id.buttonSendSim2)).setEnabled((_operatorId2 < 0) ? false : true);
    }

    private void ShowAlert(String paramString1, String paramString2) {
        AlertDialog localAlertDialog = new Builder(this).create();
        localAlertDialog.setTitle(paramString1);
        localAlertDialog.setMessage(String.format(paramString2, new Object[0]));
        localAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
            }
        });
        localAlertDialog.show();
    }

    private void TrySetContactName(Contact paramContact) {
        try {
            Cursor localCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "data1='" + paramContact.Phone + "'", null, null);
            if (localCursor.getCount() > 0) {
                localCursor.moveToFirst();
                String str = localCursor.getString(localCursor.getColumnIndex("display_name"));
                if ((str != null) && (str != "")) {
                    paramContact.Name = str;
                }
            }
            localCursor.close();
            return;
        } catch (Exception localException) {
            return;
        }
    }

    protected void DialNumber(String paramString, int sim) {
        if ((paramString == null) || (paramString.length() <= 0)) {
            return;
        }
        Operator localOperator = Common.getOperatorById((sim == 1) ? _operatorId : _operatorId2);
        if (localOperator == null) {
            ShowAlert(getString(R.string.error_send_mess), String.format(getString(R.string.check_setting_operator), new Object[]{paramString}));
            return;
        }
        String str1 = getValidNumber(paramString);
        if (str1 == null) {
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = paramString;
            arrayOfObject[1] = localOperator.Name;
            ShowAlert(getString(R.string.error_send_mess), String.format(getString(R.string.error_number_operator), arrayOfObject));
            return;
        }
        String str2 = Uri.encode(String.format(localOperator.CodeTemplate, new Object[]{str1}));
        call(str2, sim);
//        Intent callintent = new Intent("android.intent.action.CALL");
//        callintent.setData(Uri.parse("tel:" + str2));
//        try {
//            startActivity(callintent);
//            MainActivity.this.finish();
//            return;
//        } catch (ActivityNotFoundException localActivityNotFoundException) {
//            ShowAlert(getString(R.string.error_send_mess), getString(R.string.error_device_call));
//        }

//************************
//        Intent localIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + str2));
//        try {
//            startActivityForResult(localIntent, 100500);
//            return;
//        } catch (ActivityNotFoundException localActivityNotFoundException) {
//            ShowAlert("Отправка запроса невозможна", "Ваше устройство не поддерживает телефонные вызовы");
//        }
    }

    protected void DialNumber(Contact paramContact) {
        this._dialled = true;
        String str1 = paramContact.Phone;
        String str2 = paramContact.Name;
        if (str1.length() <= 0) return;

        Operator operator = Common.getOperatorById(_operatorId);
        if (operator == null) {
            ShowAlert(getString(R.string.error_send_mess), String.format(getString(R.string.check_setting_operator), new Object[]{str1}));
            return;
        }

        String str3 = getValidNumber(str1);
        if (str3 == null) {
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = str1;
            arrayOfObject[1] = operator.Name;
            ShowAlert(getString(R.string.error_send_mess), String.format(getString(R.string.error_number_operator), arrayOfObject));
            return;
        }

        String str4a = "";
        if (_operatorId2 >= 0) {
            Operator operator2 = Common.getOperatorById(_operatorId2);
            if (operator2 == null) {
                ShowAlert(getString(R.string.error_send_mess), String.format(getString(R.string.check_setting_operator), new Object[]{str1}));
                return;
            }
            String str3a = getValidNumber(str1);
            if (str3a == null) {
                Object[] arrayOfObject = new Object[2];
                arrayOfObject[0] = str1;
                arrayOfObject[1] = operator2.Name;
                ShowAlert(getString(R.string.error_send_mess), String.format(getString(R.string.error_number_operator), arrayOfObject));
                return;
            }
            str4a = Uri.encode(String.format(operator2.CodeTemplate, new Object[]{str3a}));

            try {
                String str5a = String.format(operator2.CodeTemplate, new Object[]{str3a});
                ((EditText) findViewById(R.id.editText_sendSim2)).setText(str5a);
            } catch (Exception e) {
                Editor localEditor = getSharedPreferences("SETTINGS", 0).edit();
                localEditor.putString("LAST_REQUEST_NAME", str2);
                localEditor.putString("LAST_REQUEST_PHONE", str1);
                localEditor.commit();
            }
        }

        String str4 = Uri.encode(String.format(operator.CodeTemplate, new Object[]{str3}));
        //        Intent localIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + str4));
//        try {
//            startActivityForResult(localIntent, 100500);
//            return;
//        } catch (ActivityNotFoundException localActivityNotFoundException) {
//            ShowAlert("Отправка запроса невозможна", "Ваше устройство не поддерживает телефонные вызовы");
//        }
        try {
            String str5 = String.format(operator.CodeTemplate, new Object[]{str3});
            ((EditText) findViewById(R.id.editText_sendSim1)).setText(str5);
            onSendDial(str4, str4a, paramContact);
            return;
        } catch (Exception e) {
            Editor localEditor = getSharedPreferences("SETTINGS", 0).edit();
            localEditor.putString("LAST_REQUEST_NAME", str2);
            localEditor.putString("LAST_REQUEST_PHONE", str1);
            localEditor.commit();
        }
    }

    private void onSendDial(final String phoneSim1, final String phoneSim2, Contact contact) {
        // сам вызываю телефонию...скинуть бомжа
        sel_contact = 1;
        ((TextView) findViewById(R.id.text_dial_name)).setText(contact.Name);
        ((ImageButton) findViewById(R.id.buttonSendSim1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (sel_contact <= -1) return;
                call(phoneSim1, 1);
//                Intent callintent = new Intent("android.intent.action.CALL");
//                callintent.setData(Uri.parse("tel:" + phone));
//                startActivity(callintent);
//                ((EditText) findViewById(R.id.editText_sendSim1)).setText("");
                MainActivity.this.finish();
            }
        });

        ((ImageButton) findViewById(R.id.buttonSend_Clear)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                sel_contact = -1;
                ((EditText) findViewById(R.id.editText_sendSim1)).setText("");
                ((EditText) findViewById(R.id.editText_sendSim2)).setText("");
                ((TextView) findViewById(R.id.text_dial_name)).setText("");
            }
        });

        ((ImageButton) findViewById(R.id.buttonSendSim2)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (sel_contact <= -1) return;
                call(phoneSim2, 2);
//                Intent callintent = new Intent("android.intent.action.CALL");
//                callintent.setData(Uri.parse("tel:" + phone));
//                startActivity(callintent);
                MainActivity.this.finish();
            }
        });

    }

    private void call(String phone, int sim) {
        Intent call = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        call.setData(Uri.parse("tel:" + phone.replaceAll("#", Uri.encode("#"))));
        call.putExtra("com.android.phone.extra.slot", sim - 1);
        call.putExtra("simSlot", sim - 1);
        startActivity(call);
    }

    public String getValidNumber(String paramString) {
        return Common.getValidNumber(paramString, this._operatorId);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.PICK_PHONE_RESULT && resultCode == -1) {
            Uri uri = data.getData();
            if ((data == null) || (uri == null)) {
                return;
            }
            if (uri != null) {
                Cursor contacts = null;
                try {
                    contacts = getContentResolver().query(uri, new String[]{"data1", "data2", "display_name"}, null, null, null);
                    if (contacts != null && contacts.moveToFirst()) {
                        DialNumber(new Contact(contacts.getString(2), contacts.getString(0), 0));
                    }
                    if (contacts != null) {
                        contacts.close();
                    }
                } catch (Throwable th) {
                    if (contacts != null) {
                        contacts.close();
                    }
                }
            }
        }
        if (requestCode == this.SPLASH_ACTIVITY_RESULT) {
            SetOperatorText();
            return;
        }
        if (requestCode != this.ENTER_NUMBER_ACTIVITY_REQUEST || resultCode != -1) {
//            finish();
            return;
        }
        DialNumber(data.getStringExtra("number"), data.getIntExtra("numberSIM", 1));
    }

    public void onCreate(Bundle paramBundle) {
        SharedPreferences localSharedPreferences1 = getSharedPreferences("SETTINGS", 0);
        _number_starts = localSharedPreferences1.getInt("NUMBER_STARTS", 1);
        _notificated_recived = localSharedPreferences1.getBoolean("NOTIFICATION_RECIVED", false);
        Editor localEditor = localSharedPreferences1.edit();
        localEditor.putInt("NUMBER_STARTS", 1 + this._number_starts);
        localEditor.commit();
        SharedPreferences localSharedPreferences2 = getSharedPreferences("SETTINGS", 0);
        boolean bool = localSharedPreferences2.getBoolean("IS_FIRST_RUN", true);
        _operatorId = localSharedPreferences2.getInt("CURRENT_OPERATOR", -1);
//        _operatorId2 = localSharedPreferences2.getInt("SECOND_OPERATOR", -1);
        if ((bool) || (this._operatorId == -1)) {
            startActivityForResult(new Intent(super.getBaseContext(), SplashActivity.class), this.SPLASH_ACTIVITY_RESULT);
        }
        super.onCreate(paramBundle);
        requestWindowFeature(4);
        setContentView(R.layout.main);
        setFeatureDrawableResource(4, R.drawable.ic_launcher24);
        SetOperatorText();
        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
                localIntent.setType("vnd.android.cursor.item/phone_v2");
                startActivityForResult(localIntent, PICK_PHONE_RESULT);
            }
        });
        ((ImageButton) findViewById(R.id.buttonEnterNumber)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Intent localIntent = new Intent(MainActivity.this.getApplicationContext(), EnterNumberActivity.class);
                startActivityForResult(localIntent, ENTER_NUMBER_ACTIVITY_REQUEST);
            }
        });
        ((ListView) findViewById(R.id.listView1)).setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
                Contact localContact = (Contact) ((ListView) paramAnonymousAdapterView).getItemAtPosition(paramAnonymousInt);
                DialNumber(localContact);
            }
        });

//        ((EditText) findViewById(R.id.editText_sendSim2)).setEnabled((_operatorId2 < 0) ? false : true);
//        ((ImageButton) findViewById(R.id.buttonSendSim2)).setEnabled((_operatorId2 < 0) ? false : true);
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.icon_menu, paramMenu);
        return true;
    }

    public void onDismiss(DialogInterface paramDialogInterface) {
        if (paramDialogInterface == this.currentDialog) {
            this.currentDialog = null;
        }
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(paramMenuItem);
            case R.id.quit: //
                finish();
                return true;

            case R.id.settings: //
                startActivityForResult(new Intent(super.getBaseContext(), SplashActivity.class), this.SPLASH_ACTIVITY_RESULT);
                return true;

            case R.id.mail: //
                Intent localIntent2 = new Intent("android.intent.action.SEND");
                localIntent2.setType("message/rfc822");
                localIntent2.putExtra("android.intent.extra.EMAIL", new String[]{"valikthlw8s@gmail.com"});
                localIntent2.putExtra("android.intent.extra.SUBJECT", getString(R.string.prog_name));
                localIntent2.putExtra("android.intent.extra.TEXT", getString(R.string.goodmorning));
                try {
                    startActivity(Intent.createChooser(localIntent2, getString(R.string.letter_programer)));
                    return true;
                } catch (ActivityNotFoundException localActivityNotFoundException2) {
                    Toast.makeText(this, R.string.mail_client_not_create, Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.share: //
                Intent localIntent1 = new Intent("android.intent.action.SEND");
                localIntent1.setType("text/plain");
                localIntent1.putExtra("android.intent.extra.SUBJECT", getString(R.string.text_0));
                localIntent1.putExtra("android.intent.extra.TEXT", getString(R.string.text_1));
                try {
                    startActivity(Intent.createChooser(localIntent1, getString(R.string.share_with_friends)));
                    return true;
                } catch (ActivityNotFoundException localActivityNotFoundException1) {
                    Toast.makeText(this, R.string.mail_client_not_create, Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.comments: //
                startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse(getString(R.string.address_program))));
                return true;
        }
    }

    protected void onResume() {
        super.onResume();
        String[] arrstring = {"number", "date", "duration", "type", "name"};
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, arrstring, null, null, "date DESC");
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        while (cursor != null && cursor.moveToNext()) {
            String string = cursor.getString(0);
            String string2 = cursor.getString(4);
            Long l = cursor.getLong(1);
            String string3 = getValidNumber(string);
            if (string3 == null || hashMap.containsKey((Object) string3)) continue;
            hashMap.put((Object) string3, (Object) string2);
            if (string2 == null) {
                string2 = string;
            }

            long l2 = l;
            Contact contact = new Contact(string2, string, l2);
            if (arrayList.size() > 20) break;
            arrayList.add((Object) contact);
        }
        cursor.close();
        Cursor cursor2 = getApplicationContext().getContentResolver().query(Uri.parse((String) "content://sms/"), null, null, null, "date DESC LIMIT 100");
        cursor2.moveToFirst();
        while (cursor2 != null && cursor2.moveToNext()) {
            String string;
            String string4 = string = cursor2.getString(cursor2.getColumnIndex("address"));
            Long l = cursor2.getLong(cursor2.getColumnIndex("date"));
            String string5 = getValidNumber(string);
            if (string5 == null || hashMap.containsKey((Object) string5)) continue;
            hashMap.put((Object) string5, (Object) string4);
            if (string4 == null) {
                string4 = string;
            }
            long l3 = l;
            Contact contact = new Contact(string4, string, l3);
            TrySetContactName(contact);
            arrayList.add((Object) contact);
            if (arrayList.size() <= 40) continue;
        }
        cursor2.close();
        ListView listView = (ListView) findViewById(R.id.listView1);
        Collections.sort((List) arrayList, (Comparator) new Comparator<Contact>() {
            public int compare(Contact contact, Contact contact2) {
                if (contact.Datetime > contact2.Datetime) {
                    return -1;
                }
                if (contact.Datetime < contact2.Datetime) {
                    return 1;
                }
                return 0;
            }
        });
        listView.setAdapter(new ContactArrayAdapter(this, R.layout.contact_row, (List<Contact>) arrayList));
        try {
            CheckComments();
            return;
        } catch (Exception e) {
            return;
        }

//        Cursor localCursor2 = null;
//        if ((localCursor1 == null) || (!localCursor1.moveToNext())) {
//            localCursor1.close();
//            localCursor2 = getApplicationContext().getContentResolver().query(Uri.parse("content://sms/"), null, null, null, "date DESC LIMIT 100");
//            localCursor2.moveToFirst();
//        }
//        String str1;
//        String str2;
//        Long localLong1;
//        String str3;
////        do {
//        if ((localCursor2 == null) || (!localCursor2.moveToNext())) {
//            localCursor2.close();
//            ListView localListView = (ListView) findViewById(R.id.listView1);
//            Collections.sort(localArrayList, new Comparator() {
//                @Override
//                public int compare(Object obj1, Object obj2) {
//                    return compare((Contact) obj1, (Contact) obj2);
//                }
//
//                public int compare(Contact paramAnonymousContact1, Contact paramAnonymousContact2) {
//                    if (paramAnonymousContact1.Datetime > paramAnonymousContact2.Datetime) {
//                        return -1;
//                    }
//                    if (paramAnonymousContact1.Datetime < paramAnonymousContact2.Datetime) {
//                        return 1;
//                    }
//                    return 0;
//                }
//            });
//            localListView.setAdapter(new ContactArrayAdapter(this, R.layout.contact_row, localArrayList));
//        }
//        try {
//            CheckComments();
//            return;
//        } catch (Exception localException) {
//        }
//        String str4 = localCursor1.getString(0);
//        String str5 = localCursor1.getString(4);
//        Long localLong2 = Long.valueOf(localCursor1.getLong(1));
//        String str6 = getValidNumber(str4);
//        if ((str6 == null) || (localHashMap.containsKey(str6))) {
//            return;
//        }
//        localHashMap.put(str6, str5);
//        if (str5 != null) {
//        }
//        for (; ; ) {
//            long l2 = localLong2.longValue();
//            Contact localContact2 = new Contact(str5, str4, l2);
//            if (localArrayList.size() > 20) {
//                break;
//            }
//            localArrayList.add(localContact2);
//
//            str5 = str4;
//            break;
//        }
//        str1 = localCursor2.getString(localCursor2.getColumnIndex("address"));
//        str2 = str1;
//        localLong1 = Long.valueOf(localCursor2.getLong(localCursor2.getColumnIndex("date")));
//        str3 = getValidNumber(str1);
////        } while
//        if ((str3 == null) || (localHashMap.containsKey(str3))) {
//            return;
//        }
//
//        localHashMap.put(str3, str2);
//        if (str2 != null) {
//
//            long l1 = localLong1.longValue();
//            Contact localContact1 = new Contact(str2, str1, l1);
//            TrySetContactName(localContact1);
//            localArrayList.add(localContact1);
//            if (localArrayList.size() <= 40) {
//                return;
//            }
//            str2 = str1;
//        }
//    }
//        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for Activity#requestPermissions for more details.
//            return;
//        }
//        Cursor cursor = getContentResolver().query(Calls.CONTENT_URI,
//                new String[]{"number", "date", "duration", "type", "name"},
//                null, null, "date DESC");
//        List<Contact> contacts = new ArrayList();
//        Map<String, String> existingPhones = new HashMap();
//        String phoneNumber;
//        String name;
//        Long date;
//        String tempNumber;
//        Contact contact;
//        while (cursor != null && cursor.moveToNext()) {
//            phoneNumber = cursor.getString(0);
//            name = cursor.getString(4);
//            date = Long.valueOf(cursor.getLong(1));
//            tempNumber = getValidNumber(phoneNumber);
//            if (!(tempNumber == null || existingPhones.containsKey(tempNumber))) {
//                existingPhones.put(tempNumber, name);
//                if (name == null) {
//                    name = phoneNumber;
//                }
//                contact = new Contact(name, phoneNumber, date.longValue());
//                if (contacts.size() > 20) {
//                    break;
//                }
//                contacts.add(contact);
//            }
//        }
//        cursor.close();
//        Cursor smsCursor = getApplicationContext().getContentResolver().query(Uri.parse("content://sms/"), null, null, null, "date DESC LIMIT 100");
//        smsCursor.moveToFirst();
//        while (smsCursor != null && smsCursor.moveToNext()) {
//            phoneNumber = smsCursor.getString(smsCursor.getColumnIndex("address"));
//            name = phoneNumber;
//            date = Long.valueOf(smsCursor.getLong(smsCursor.getColumnIndex("date")));
//            tempNumber = getValidNumber(phoneNumber);
//            if (!(tempNumber == null || existingPhones.containsKey(tempNumber))) {
//                existingPhones.put(tempNumber, name);
//                if (name == null) {
//                    name = phoneNumber;
//                }
//                contact = new Contact(name, phoneNumber, date.longValue());
//                TrySetContactName(contact);
//                contacts.add(contact);
//                if (contacts.size() > 40) {
//                    break;
//                }
//            }
//        }
//        smsCursor.close();
//        ListView list = (ListView) findViewById(R.id.listView1);
//        Collections.sort(contacts, new Comparator<Contact>() {
//            @Override
//            public int compare(Contact obj1, Contact obj2) {
//                return compare((Contact) obj1, (Contact) obj2);
//            }
//
////            public int compare(Contact contact, Contact contact1)
////            {
////                if (contact.Datetime > contact1.Datetime)
////                {
////                    return -1;
////                }
////                return contact.Datetime >= contact1.Datetime ? 0 : 1;
////            }
//        });
//        list.setAdapter(new ContactArrayAdapter(this, R.layout.contact_row, contacts));
//        try {
//            CheckComments();
//        } catch (Exception e) {
//        }
    }
}
