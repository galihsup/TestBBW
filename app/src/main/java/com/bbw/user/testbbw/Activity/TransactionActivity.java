package com.bbw.user.testbbw.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bbw.user.testbbw.Model.OperatorModel;
import com.bbw.user.testbbw.Model.PulsaModel;
import com.bbw.user.testbbw.R;
import com.bbw.user.testbbw.Util.AppsController;
import com.bbw.user.testbbw.Util.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {

    TextView tvHarga;
    TextInputEditText tiePhone,tieOperator,tiePulsa;
    Button btnSimpan;
    ProgressDialog pd;
    List<OperatorModel> list_operator = new ArrayList<>();
    CharSequence data_operator[];
    List<PulsaModel> list_pulsa = new ArrayList<>();
    CharSequence data_pulsa[];
    Gson gson;
    String getUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        gson = new Gson();

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        getUserID = sharedPrefs.getString(Constant.KEY_SHAREDPREFS_USERID, null);
        Log.d("useridd : ",getUserID);
        tvHarga = (TextView) findViewById(R.id.tv_harga_pulsa);
        tiePhone = (TextInputEditText) findViewById(R.id.tie_phone);
        tieOperator = (TextInputEditText) findViewById(R.id.tie_operator);
        tiePulsa = (TextInputEditText) findViewById(R.id.tie_pulsa);
        btnSimpan = (Button) findViewById(R.id.btn_SAVE);

        getOperator();

        tieOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
                builder.setTitle("-- Pilih Operator --");
                builder.setItems(data_operator, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tieOperator.setText(data_operator[i]);
                        getPulsa(data_operator[i].toString());
                        tiePulsa.setText("Pulsa");
                        tvHarga.setText("-");
                        tiePulsa.setEnabled(true);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        tiePulsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
                builder.setTitle("-- Pilih Voucher Pulsa --");
                builder.setItems(data_pulsa, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tiePulsa.setText(data_pulsa[i]);
                        tvHarga.setText(list_pulsa.get(i).getHarga());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanTransaksi();
            }
        });
    }

    private void getOperator(){
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();
        String url = Constant.BASE_URL;
        Log.d("url : ",url);
        StringRequest req = new StringRequest(Request.Method.POST, url, successListener(), errListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "operator");
                return params;
            }
        };
        AppsController.getInstance().addToRequestQueue(req);
    }

    private Response.ErrorListener errListener() {
        return new Response.ErrorListener() {
            @Override
            public  void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e("Error", String.valueOf(error));
                Toast.makeText(TransactionActivity.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Response.Listener<String> successListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try{
                    Log.d("response", response);
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("msg");
                    Log.d("message", message);
                    if (json.getString("status").equals("0")) {
                        String jsonOutput = json.getString("operator");
                        Type listType = new TypeToken<List<OperatorModel>>(){}.getType();
                        list_operator = (List<OperatorModel>) gson.fromJson(jsonOutput, listType);
                        isiOperator();
                    } else {
                        Toast.makeText(TransactionActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void isiOperator(){
        data_operator = new CharSequence[list_operator.size()];
        for(int i = 0; i<list_operator.size(); i++){
            data_operator[i] = list_operator.get(i).getNama();
        }
    }

    private void getPulsa(final String op){
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();
        String url = Constant.BASE_URL;
        Log.d("url : ",url);
        StringRequest req = new StringRequest(Request.Method.POST, url, listenerSuccess(), errListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "voucher");
                params.put("operator", op);
                return params;
            }
        };
        AppsController.getInstance().addToRequestQueue(req);
    }

    private Response.Listener<String> listenerSuccess() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try{
                    Log.d("response", response);
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("msg");
                    Log.d("message", message);
                    if (json.getString("status").equals("1")) {
                        String jsonOutput = json.getString("voucher");
                        Type listType = new TypeToken<List<PulsaModel>>(){}.getType();
                        list_pulsa = (List<PulsaModel>) gson.fromJson(jsonOutput, listType);
                        isiPulsa();
                    } else {
                        Toast.makeText(TransactionActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void isiPulsa(){
        data_pulsa = new CharSequence[list_pulsa.size()];
        for(int i = 0; i<list_pulsa.size(); i++){
            data_pulsa[i] = list_pulsa.get(i).getPulsa();
        }
    }

    private void simpanTransaksi(){
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();
        String url = Constant.BASE_URL;
        Log.d("url : ",url);
        StringRequest req = new StringRequest(Request.Method.POST, url, listenSuccess(), errListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "transaction");
                params.put("userid", getUserID);
                params.put("operator", tieOperator.getText().toString());
                params.put("harga", tvHarga.getText().toString());
                return params;
            }
        };
        AppsController.getInstance().addToRequestQueue(req);
    }

    private Response.Listener<String> listenSuccess() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try{
                    Log.d("response", response);
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("msg");
                    Log.d("message", message);
                    if (json.getString("status").equals("1")) {
                        Toast.makeText(TransactionActivity.this,message,Toast.LENGTH_LONG).show();
                        tiePhone.setText("");
                        tieOperator.setText("Operator");
                        tiePulsa.setText("Pulsa");
                        tvHarga.setText("-");
                    } else {
                        Toast.makeText(TransactionActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
