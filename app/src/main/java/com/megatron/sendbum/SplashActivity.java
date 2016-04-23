package com.megatron.sendbum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class SplashActivity extends Activity {

    Context context;
    private boolean CARD1_EXIST = false;
    private boolean CARD2_EXIST = false;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        context = this;
        requestWindowFeature(4);
        setContentView(R.layout.splash);
        setFeatureDrawableResource(4, R.drawable.ic_launcher24);
        ArrayList localArrayList = Common.getOperators();
        ArrayList localArrayList2 = Common.getOperators();
        SharedPreferences localSharedPreferences = getSharedPreferences("SETTINGS", 0);
        boolean bool = localSharedPreferences.getBoolean("IS_FIRST_RUN", true);
        int i = localSharedPreferences.getInt("CURRENT_OPERATOR", -1);
        int i2 = localSharedPreferences.getInt("SECOND_OPERATOR", -1);
        if (bool) {
            i = tryGetOperatorIdByNetworkName();
        }
        //***************
        boolean dual = isSupportedDualSim();
        if (dual) {
            CARD1_EXIST = getSimActive(0);
            CARD2_EXIST = getSimActive(1);
        } else
            CARD1_EXIST = true;
        //**************
        if (CARD1_EXIST) {
            Spinner localSpinner = (Spinner) findViewById(R.id.spinner1);
            localSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter localArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, localArrayList);
            localArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            localSpinner.setAdapter(localArrayAdapter);

            Editor localEditor = localSharedPreferences.edit();
            if (i != -1) {
                localSpinner.setSelection(Common.getOperatorPositionById(i));
                localEditor.putInt("CURRENT_OPERATOR", i);
            } else {
                localSpinner.setSelection(0);
                localEditor.putInt("CURRENT_OPERATOR", 1);
            }
            localEditor.commit();
            localSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
//                    Editor localEditor = SplashActivity.this.getSharedPreferences("SETTINGS", 0).edit();
//                    localEditor.putBoolean("IS_FIRST_RUN", false);
//                    Operator localOperator = (Operator) ((Spinner) paramAnonymousAdapterView).getSelectedItem();
//                    if (localOperator != null) {
//                        localEditor.putInt("CURRENT_OPERATOR", localOperator.Id);
//                        localEditor.commit();
//                    }
                }

                public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {
                }
            });
        }
        if (CARD2_EXIST) {
            ((TextView) findViewById(R.id.textView7)).setVisibility(View.VISIBLE);
            Spinner localSpinner2 = (Spinner) findViewById(R.id.spinner2);
            localSpinner2.setVisibility(View.VISIBLE);
            ArrayAdapter localArrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, localArrayList2);
            localArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            localSpinner2.setAdapter(localArrayAdapter2);

            Editor localEditor = localSharedPreferences.edit();
            if (i2 != -1) {
                localSpinner2.setSelection(Common.getOperatorPositionById(i2));
                localEditor.putInt("SECOND_OPERATOR", i2);
            } else {
                localSpinner2.setSelection(0);
                localEditor.putInt("SECOND_OPERATOR", 1);
            }
            localEditor.commit();
            localSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
//                    Editor localEditor = SplashActivity.this.getSharedPreferences("SETTINGS", 0).edit();
//                    localEditor.putBoolean("IS_FIRST_RUN", false);
//                    Operator localOperator = (Operator)((Spinner) paramAnonymousAdapterView).getSelectedItem();
//                    if (localOperator != null) {
//                        localEditor.putInt("SECOND_OPERATOR", localOperator.Id);
//                        localEditor.commit();
//                    }
                }

                public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {
                }
            });
        }

        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Editor localEditor = SplashActivity.this.getSharedPreferences("SETTINGS", 0).edit();
                localEditor.putBoolean("IS_FIRST_RUN", false);
                if (CARD1_EXIST)
                    localEditor.putInt("CURRENT_OPERATOR", ((Operator) ((Spinner) SplashActivity.this.findViewById(R.id.spinner1)).getSelectedItem()).Id);
                else localEditor.putInt("CURRENT_OPERATOR", -1);
                if (CARD2_EXIST)
                    localEditor.putInt("SECOND_OPERATOR", ((Operator) ((Spinner) SplashActivity.this.findViewById(R.id.spinner2)).getSelectedItem()).Id);
                else localEditor.putInt("SECOND_OPERATOR", -1);
                localEditor.commit();
                setResult(-1);
                finish();
            }
        });
    }

    public int tryGetOperatorIdByNetworkName() {
        try {
            TelephonyManager localTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);//"phone"
            String str1 = localTelephonyManager.getNetworkOperatorName();
            String str2 = localTelephonyManager.getSimOperatorName();
            if ((str1 == null) && (str2 == null)) {
                return -1;
            }
            Iterator localIterator = Common.getOperators().iterator();
            while (localIterator.hasNext()) {
                Operator localOperator = (Operator) localIterator.next();
                if ((str1 != null) && (str1.toLowerCase().contains(localOperator.TechnicalName))) {
                    return localOperator.Id;
                }
                if ((str2 != null) && (str2.toLowerCase().contains(localOperator.TechnicalName))) {
                    int i = localOperator.Id;
                    return i;
                }
            }
        } catch (Exception localException) {
        }
        return -1;
    }

    public static boolean isSupportedDualSim() {
        try {
            Class.forName("com.mediatek.telephony.TelephonyManagerEx");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean getSimActive(int sim_num) {
        try {
            Class<?> c = Class.forName("com.mediatek.telephony.TelephonyManagerEx");
            String ss = (String) c.getMethod("getLine1Number", Integer.TYPE).invoke(c.getConstructor(android.content.Context.class).newInstance(context), sim_num);
            String s2 = (String) c.getMethod("getSimOperatorName", Integer.TYPE).invoke(c.getConstructor(android.content.Context.class).newInstance(context), sim_num);
//            getNetworkOperatorName
            boolean r1 = !ss.trim().equals("");
            boolean r2 = !s2.trim().equals("");

            return (r1 && r2);
        } catch (Exception e) {
            return false;
        }
    }
}
