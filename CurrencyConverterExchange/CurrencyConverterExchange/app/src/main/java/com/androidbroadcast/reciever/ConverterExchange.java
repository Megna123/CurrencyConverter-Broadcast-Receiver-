package com.androidbroadcast.reciever;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConverterExchange extends Activity {
    public static final String tag = "ConverterExchange";
    public static String amount;
    public static String type;
    public TextView amount_to_convert;
    public TextView conversion_type;
    public TextView in_progess;
    public static final String CUSTOM_BROADCAST_ACTION = "com.androidbroadcast.reciever.CUSTOM_BROADCAST";
    public static final String CUSTOM_RECEIVE_CONVERTED_DATA_BROADCAST_ACTION = "com.androidbook.bcr.CUSTOM_BROADCAST";
    BroadcastReceiver broadcastreceiver;
    public static String currentRate;
    //URL url=new URL("https://api.exchangeratesapi.io/latest?base=USD");;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        amount_to_convert = (TextView) findViewById(R.id.amount_to_convert);
        conversion_type = (TextView) findViewById(R.id.type_to_convert);
        in_progess = (TextView) findViewById(R.id.in_progess);
        Log.d("create", "onCreated: ");
        broadcastreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Register", "onReceive: registered");
                Bundle b = intent.getExtras();
                Log.d("Received", "onReceive: " + b);
                if (b != null) {
                    amount = b.getString("amount");
                    type = b.getString("type");
                    amount_to_convert.setText(amount);
                    conversion_type.setText(type);
                }
            }
        };
        IntentFilter filter = new IntentFilter(CUSTOM_BROADCAST_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(broadcastreceiver, filter);
    }

    public void convert(View v) {
        Log.d("calci", "convert: " + currencyConverter(amount, type));
        String convertedAmount = String.valueOf(currencyConverter(amount, type));
        Intent intent = new Intent(CUSTOM_RECEIVE_CONVERTED_DATA_BROADCAST_ACTION);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("convertedAmount", convertedAmount);
        Log.d("broadcast message", "onClick: " + intent.getExtras());
        sendBroadcast(intent);
        in_progess.setText("Applied Currency Conversion  succesfully!");
    }

    public void close(View v) {
        ConverterExchange.this.finish();
    }

    public double currencyConverter(String amount, String type) {
        double convertedAmount = 0;
        String asyncResponse = "";
        int dollar_amount = Integer.valueOf(amount);
        try {
            if (type.equalsIgnoreCase("Indian Rupee")) {
                Log.d("called", "currencyConverter: convertedAmount");
                asyncResponse = new fetchCurrentRate().execute(type).get();
            } else if (type.equalsIgnoreCase("British Pound")) {
                asyncResponse = new fetchCurrentRate().execute(type).get();
            } else if (type.equalsIgnoreCase("Euro")) {
                asyncResponse = new fetchCurrentRate().execute(type).get();
            }
        } catch (Exception e) {
            Log.d("Exception", "currencyConverter: " + e);
        }
        Log.d("Message", "currencyConverter: " + asyncResponse);
        convertedAmount = (Double.valueOf(asyncResponse) * dollar_amount);
        Log.d("Converted Amount", "currencyConverter: " + convertedAmount);
        return convertedAmount;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastreceiver);
    }
}

class fetchCurrentRate extends AsyncTask<String, Void, String> {
    private Exception exception;

    protected String doInBackground(String... urls) {
        String responseString = "";
        String currentRate = "";
        try {
            Log.d("Entered call exchange", "callExchanger: " + urls[0]);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet("https://api.exchangeratesapi.io/latest?base=USD"));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                JSONObject json = new JSONObject(responseString);
                JSONObject rateObject = new JSONObject();
                rateObject = json.getJSONObject("rates");
                Log.d("background", "doInBackground: " + responseString);
                Log.d("background", "doInBackground: " + rateObject.get("INR"));
                if (urls[0].equalsIgnoreCase("EURO")) {
                    currentRate = rateObject.get("EUR").toString();
                } else if (urls[0].equalsIgnoreCase("BRITISH POUND")) {
                    currentRate = rateObject.get("GBP").toString();
                } else if (urls[0].equalsIgnoreCase("INDIAN RUPEE")) {
                    currentRate = rateObject.get("INR").toString();
                }
                out.close();
                //..more logic
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
            Log.d("Exception", "doInBackground: " + e);
        } catch (IOException e) {
            //TODO Handle problems..
            Log.d("Exception", "doInBackground: " + e);
        } catch (JSONException e) {
            Log.d("JSON Exception", "doInBackground:" + e);
        }
        return currentRate;
    }


    protected void onPostExecute(String response) {
        ConverterExchange.currentRate = response;
        Log.d("Fetched", "onPostExecute: " + response);
    }
}




