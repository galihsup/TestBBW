package com.bbw.user.testbbw.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bbw.user.testbbw.R;
import com.bbw.user.testbbw.Util.AppsController;
import com.bbw.user.testbbw.Util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etUsername,etPassword;
    Button btnLogin;
    ProgressDialog pd;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();
        etUsername = (TextInputEditText) findViewById(R.id.tie_user);
        etPassword = (TextInputEditText) findViewById(R.id.tie_pass);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProses();
            }
        });
    }

    private void loginProses(){
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();
        String url = Constant.BASE_URL;
        Log.d("url : ",url);
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        StringRequest req = new StringRequest(Request.Method.POST, url, successListener(), errListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "login");
                params.put("username", username);
                params.put("password", password);

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
                Toast.makeText(MainActivity.this, String.valueOf(error), Toast.LENGTH_SHORT).show();
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
                    if (json.getString("status").equals("1")) {
                        String userid = json.getString("userid");
                        Intent intent;
                        editor.putString(Constant.KEY_SHAREDPREFS_LOGIN_STATUS, "1");
                        editor.putString(Constant.KEY_SHAREDPREFS_USERID, userid);
                        editor.commit();
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                        intent = new Intent(MainActivity.this, TransactionActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
