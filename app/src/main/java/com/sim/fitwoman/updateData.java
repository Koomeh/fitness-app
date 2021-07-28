package com.sim.fitwoman;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sim.fitwoman.R;
import com.sim.fitwoman.service.MySingleton;
import com.sim.fitwoman.utils.WSadressIP;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class updateData extends AppCompatActivity {
    EditText txtAge, txtHeight, txtWeight;
    Button btnAdd, btnCancel;
    String SPname, SPemail, SPweight ,SPheight, SPBMI, SPAge;
    private String UPDATE_PROFILE_API_URL = "http://10.0.2.2:8012/fitness/api/update-profile.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        //1:set items
        txtAge = findViewById(R.id.editText313);
        txtHeight = findViewById(R.id.editText3456);
        txtWeight = findViewById(R.id.weight);
        btnCancel = findViewById(R.id.button5cancelagain);
        btnAdd = findViewById(R.id.button4confirm);

        //2: get shared preferences data
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        txtAge.setText(preferences.getString("Age", ""));
        txtHeight.setText(preferences.getString("Height", ""));
        txtWeight.setText(preferences.getString("Weight", ""));

        //3: set cancel btn
        btnCancel.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }
        );

        //4: confirm btn
        btnAdd.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!txtAge.getText().toString().equals("") && !txtHeight.getText().toString().equals("") && !txtWeight.getText().toString().equals("") ){
                     updateData();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Enter your height and/or age and/or weight",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    public void updateData(){
        final String   URL = "http://"+ WSadressIP.WSIP+"/FitWomanServices/MUpdateAgeHeight.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_PROFILE_API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);

                    if(json.getString("errorfound").equals("0")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SPheight = preferences.getString("height", "");
                        SharedPreferences.Editor prefsEditr = preferences.edit();
                        prefsEditr.putString("Height", json.getString("Height"));
                        prefsEditr.putString("Weight", json.getString("Weight"));
                        prefsEditr.putString("Age", json.getString("Age"));
                        prefsEditr.putString("BMI", json.getString("BMI"));
                        prefsEditr.apply();

                        Intent i = new Intent(updateData.this, Home.class);
                        i.putExtra("openprofil", "open");

                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException ex)
                {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"failed to add",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Age", txtAge.getText().toString());
                params.put("Weight",txtWeight.getText().toString());
                params.put("Height",txtHeight.getText().toString());

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("UserId",preferences.getString("UserId", ""));
                params.put("Email",preferences.getString("Email", ""));
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        /////////////////
    }

    public String getBMI(String TWeight,String THeight){
        Float FWeight = Float.valueOf(TWeight);
        Float FHeight = Float.valueOf(THeight);
        Float IMC = 10000 *(FWeight/(FHeight * FHeight));
        String Res="";
        if (IMC < 18.5){
            Res = "1";
        } else if(IMC > 18.5 && IMC < 25){
            Res = "2";
        } else if(IMC > 25 ){
            Res = "3";
        }
        return Res;
    }
}
