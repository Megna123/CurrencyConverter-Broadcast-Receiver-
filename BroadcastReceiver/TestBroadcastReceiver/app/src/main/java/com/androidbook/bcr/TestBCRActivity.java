package com.androidbook.bcr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestBCRActivity extends Activity {
    public static final String tag = "TestBCRActivity";
    public static String type;
    public TextView type_selected;
    public EditText amountText;
    public TextView conversion_result;
    public static final String CUSTOM_SEND_DATA_BROADCAST_ACTION = "com.androidbroadcast.reciever.CUSTOM_BROADCAST";
    public static final String CUSTOM_RECEIVE_CONVERTED_DATA_BROADCAST_ACTION = "com.androidbook.bcr.CUSTOM_BROADCAST";
    public static String amount;
    public static String converted_amount;
    BroadcastReceiver broadcastreceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        amountText = (EditText) findViewById(R.id.editamount);
        amount = amountText.getText().toString();
        Log.d("Amount", "onCreate: " + amount);
        type_selected = (TextView) findViewById(R.id.type_selected);
        conversion_result = (TextView) findViewById(R.id.conversion_result);
        Button eurobtn = (Button) findViewById(R.id.btn_euro);
        eurobtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                type = "Euro";
                displayedForUser(type);
            }
        });
        Button poundbtn = (Button) findViewById(R.id.btn_pound);
        poundbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                type = "British Pound";
                displayedForUser(type);
            }
        });
        Button rupeebtn = (Button) findViewById(R.id.btn_rupee);
        rupeebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                type = "Indian Rupee";
                displayedForUser(type);
            }
        });


        Button convertbtn = (Button) findViewById(R.id.convert_btn);
        convertbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText amount_to_convert = (EditText) findViewById(R.id.editamount);
                amount = amount_to_convert.getText().toString();
                if (type == null) {
                    type_selected.setText("Please select currency type");
                } else {
                    Intent intent = new Intent(CUSTOM_SEND_DATA_BROADCAST_ACTION);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.putExtra("amount", amount);
                    intent.putExtra("type", type);
                    Log.d("broadcast message", "onClick: " + intent.getExtras());
                    sendBroadcast(intent);
                }
            }
        });

        broadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Register", "onReceive: registered");
                Bundle b = intent.getExtras();
                Log.d("Recieved", "onReceive: "+b);
                if (b != null) {
                    converted_amount=b.getString("convertedAmount");
                    conversion_result.setText("Dollar Amount $ "+amount+" converted to "+converted_amount+" "+type);
                }
            }
        };
        IntentFilter filter = new IntentFilter(CUSTOM_RECEIVE_CONVERTED_DATA_BROADCAST_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(broadcastreceiver, filter);

    }

    public void displayedForUser(String type) {
        conversion_result.setText("");
        type_selected.setText("Selected currency type : " + type );
    }

    public void close(View v) {
    TestBCRActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastreceiver);
    }
}

