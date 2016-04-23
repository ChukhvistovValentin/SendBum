package com.megatron.sendbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class EnterNumberActivity extends Activity {
    private int _operatorId = -1;
    private int _operatorId2 = -1;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        requestWindowFeature(4);
        setContentView(R.layout.enter_number);
        setFeatureDrawableResource(4, R.drawable.ic_launcher24);
        _operatorId = getSharedPreferences("SETTINGS", 0).getInt("CURRENT_OPERATOR", -1);
        _operatorId2 = getSharedPreferences("SETTINGS", 0).getInt("SECOND_OPERATOR", -1);

        Operator localOperator = Common.getOperatorById(this._operatorId);

        if ((localOperator == null)) {
            finish();
            return;
        }

        String text = (_operatorId > 0) ? Common.getOperatorById(this._operatorId).Name : "";
        text = text + ((!text.trim().equals("") && (_operatorId2 > 0)) ? " / " : "");
        text = text + ((_operatorId2 > 0) ? Common.getOperatorById(this._operatorId2).Name : "");
        setTitle(" " + getString(R.string.app_name) + " - " + text);
//        setTitle(" " + getString(R.string.app_name) + " - " + localOperator.Name);
        getWindow().setSoftInputMode(4);
        ((Button) findViewById(R.id.button1)).setEnabled(false);
        ((Button) findViewById(R.id.button2)).setEnabled(false);
        ((TextView) findViewById(R.id.textNumberMessage)).setText(R.string.set_of_mobile_numbers);
        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Intent localIntent = new Intent();
                localIntent.putExtra("number", ((EditText) findViewById(R.id.editText1)).getText().toString());
                localIntent.putExtra("numberSIM", 1);
                EnterNumberActivity.this.setResult(-1, localIntent);
                EnterNumberActivity.this.finish();
            }
        });

        ((Button) findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                Intent localIntent = new Intent();
                localIntent.putExtra("number", ((EditText) findViewById(R.id.editText1)).getText().toString());
                localIntent.putExtra("numberSIM", 2);
                EnterNumberActivity.this.setResult(-1, localIntent);
                EnterNumberActivity.this.finish();
            }
        });

        ((EditText) findViewById(R.id.editText1)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramAnonymousEditable) {
                String str = paramAnonymousEditable.toString();
                if (Common.getValidNumber(str, EnterNumberActivity.this._operatorId) == null) {
                    ((Button) EnterNumberActivity.this.findViewById(R.id.button1)).setEnabled(false);
                    ((Button) EnterNumberActivity.this.findViewById(R.id.button2)).setEnabled(false);
                    if (str.length() <= 0) {
                        ((TextView) EnterNumberActivity.this.findViewById(R.id.textNumberMessage)).setText(R.string.set_of_mobile_numbers);
                        return;
                    }
                    if ((str.length() > 0) && (str.length() < 6)) {
                        ((TextView) EnterNumberActivity.this.findViewById(R.id.textNumberMessage)).setText(R.string.number_not_create);
                        return;
                    }
                    if ((str.length() >= 6) && (str.length() < 10)) {
                        ((TextView) EnterNumberActivity.this.findViewById(R.id.textNumberMessage)).setText(R.string.number_pochti_create);
                        return;
                    }
                    ((TextView) EnterNumberActivity.this.findViewById(R.id.textNumberMessage)).setText(R.string.maybi_number_error);
                    return;
                }
                ((Button) EnterNumberActivity.this.findViewById(R.id.button1)).setEnabled(true);
                ((Button) findViewById(R.id.button2)).setEnabled((_operatorId2 < 0) ? false : true);
                ((TextView) EnterNumberActivity.this.findViewById(R.id.textNumberMessage)).setText(R.string.press_ok_send);
            }

            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }

            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
            }
        });
    }
}
