package com.sim.fitwoman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ResponseDelivery;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sim.fitwoman.R;
import com.sim.fitwoman.service.MySingleton;
import com.sim.fitwoman.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Sign_up extends AppCompatActivity implements Response.Listener, Response.ErrorListener {
    Button btnCreateAccount;
    EditText etName , etEmail , etPwd, etMobileNo;
    private String REGISTRATION_API_URL = "http://10.0.2.2:8000/fitness/api/registration.php";
    //private String REGISTRATION_API_URL = "http://192.168.226.222/fitness/api/registration.php";
    private RequestQueue mQueue;
    public static final String REQUEST_TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // add toolbar back button
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.m31);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   finish();
                Intent i = new Intent(Sign_up.this, MainActivity.class);
                i.putExtra("okok", "ok");

                startActivity(i);
            }
        });

        btnCreateAccount = (Button) findViewById(R.id.email_sign_up_button);
        etName = (EditText) findViewById(R.id.input_name);
        etEmail = (EditText) findViewById(R.id.email);
        etPwd = (EditText) findViewById(R.id.password);
        etMobileNo = (EditText) findViewById(R.id.mobileno);


/*        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk";
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method
                .POST, REGISTRATION_API_URL,
                new JSONObject(), this, this);


        jsonRequest.setTag(REQUEST_TAG);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(jsonRequest);
            }
        });*/
        RegisterProcess();
    }
    void RegisterProcess(){


        btnCreateAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if(!etName.getText().toString().equals("") && !etEmail.getText().toString().equals("") && !etPwd.getText().toString().equals("")){

                        //dontExist();
                        addThisUser();

                    }else{
                    Toast.makeText(getApplicationContext(),"Fill all data before register",Toast.LENGTH_SHORT).show();
                    }

                    }
                }
        );
    }



    public void dontExist(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ WSadressIP.WSIP+"/FitWomanServices/MIfUserEmailExist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            if(array.length() == 0){
                                addThisUser();
                            }else{
                                Toast.makeText(getApplicationContext(),"YOUR EMAIL ALREADY EXIST",Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                params.put("Content-Type","application/x-www-form-urlencoded");


                params.put("emailUser", etEmail.getText().toString());

                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }



    public void addThisUser(){
        //final String   URL = "http://"+ WSadressIP.WSIP+"/FitWomanServices/addUserFromApp.php";
        final String   URL = REGISTRATION_API_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    //if (json.getString("success")) {

                        Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_LONG);
                        Intent intent = new Intent(Sign_up.this, MainActivity.class);

                        startActivity(intent);
                        ////////////////////
                    //}
                }
                catch (JSONException ex)
                {}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"failed to Register",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Name", etName.getText().toString());
                params.put("Email",etEmail.getText().toString());
                params.put("Password",etPwd.getText().toString());
                params.put("MobileNo",etMobileNo.getText().toString());
                params.put("UserType", "User");
                //params.put("photo", "nothing");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


/*    @Override
    protected void onStart() {
        super.onStart();
        // Instantiate the RequestQueue.
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk";
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method
                .GET, url,
                new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(jsonRequest);
            }
        });
    }*/
    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        String message = error.getMessage();
       // mTextView.setText(error.getMessage());
    }
    @Override
    public void onResponse(Object response) {
        String message = response.toString();
//        mTextView.setText("Response is: " + response);
//        try {
//            mTextView.setText(mTextView.getText() + "\n\n" + ((JSONObject) response).getString
//                    ("name"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}
