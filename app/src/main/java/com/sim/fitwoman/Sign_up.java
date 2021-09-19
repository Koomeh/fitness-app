package com.sim.fitwoman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
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
    EditText etName , etEmail , etPwd, etMobileNo, etAge, etWeight, etHeight;
    private String REGISTRATION_API_URL = "http://10.0.2.2:8012/fitness/api/registration.php";
    //private String REGISTRATION_API_URL = "http://192.168.198.222/fitness/api/registration.php";
    //private String REGISTRATION_API_URL = "http://192.168.0.101/fitness/api/registration.php";
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
        etAge = (EditText) findViewById(R.id.age);
        etWeight = (EditText) findViewById(R.id.weight);
        etHeight = (EditText) findViewById(R.id.height);


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
//PUTTING RANGE VALUES FOR THE SIGN UP SCREEN
                    while(true) {
                        if(etEmail.getText().toString().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter Email", Toast.LENGTH_LONG).show();
                            break;
                        }

                        boolean validEmail = isValidEmail(etEmail.getText().toString());
                        if(validEmail == false)
                        {
                            Toast.makeText(getApplicationContext(),"Wrong Email format", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etPwd.getText().toString().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter Password", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etPwd.getText().toString().length()<8)
                        {
                            Toast.makeText(getApplicationContext(),"Password too short, password should have 8 characters", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etPwd.getText().toString().length()>8)
                        {
                            Toast.makeText(getApplicationContext(),"Password too long, password should have 8 characters", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etName.getText().toString().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter Name", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etAge.getText().toString().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter Age", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(Integer.parseInt(etAge.getText().toString()) < 13)
                        {
                            Toast.makeText(getApplicationContext(),"Age is invalid, should be more than 13", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(Integer.parseInt(etAge.getText().toString()) > 100)
                        {
                            Toast.makeText(getApplicationContext(),"Age is invalid, should be less than 100", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etMobileNo.getText().toString().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter Mobile No", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etMobileNo.getText().toString().length() > 15 )
                        {
                            Toast.makeText(getApplicationContext(),"MobileNo too Long, less than 10 characters", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(etMobileNo.getText().toString().length() < 10 ) {
                            Toast.makeText(getApplicationContext(), "MobileNo too Short, should be 10 characters", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(Integer.parseInt(etWeight.getText().toString()) > 300)
                        {
                            Toast.makeText(getApplicationContext(),"Weight is invalid, should be less than 300KG", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(Integer.parseInt(etWeight.getText().toString()) < 30)
                        {
                            Toast.makeText(getApplicationContext(),"Weight is invalid, should be more than 30KG", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(Double.parseDouble(etHeight.getText().toString()) < 62.8)
                        {
                            Toast.makeText(getApplicationContext(),"Height is invalid, should be more than 62.8 CM", Toast.LENGTH_LONG).show();
                            break;
                        }
                        if(Double.parseDouble(etHeight.getText().toString()) > 213)
                        {
                            Toast.makeText(getApplicationContext(),"Height is invalid, should be less than 213 CM", Toast.LENGTH_LONG).show();
                            break;
                        }
                        addThisUser();
                        break;
                    }

                }else{
                Toast.makeText(getApplicationContext(),"Fill all data before register",Toast.LENGTH_SHORT).show();
                }

                }
            }
        );
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
                params.put("Age",etAge.getText().toString());
                params.put("Weight",etWeight.getText().toString());
                params.put("Height",etHeight.getText().toString());
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
