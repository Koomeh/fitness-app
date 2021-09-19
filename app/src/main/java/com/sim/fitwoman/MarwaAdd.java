package com.sim.fitwoman;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.sim.fitwoman.R;
import com.sim.fitwoman.service.MySingleton;
import com.sim.fitwoman.utils.WSadressIP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MarwaAdd extends AppCompatActivity {
    Button btnAddActivity;
    EditText    Aduration ;
    TextView Aname;
    Integer idUser;
    String SPname , SPemail , SPweight;
    String ActiMet , ActiIcon;
    ImageView theIcon;

    private static  String ADD_PERSONAL_ACTIVITY_API_URL = "http://10.0.2.2:8012/fitness/api/add-personal-activity.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marwa_add);

        //1: get toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.m31);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        //2: get data from fragment "activity"
        String ActivityName = this.getIntent().getStringExtra("nameS");
        ActiMet = this.getIntent().getStringExtra("metS");
        ActiIcon = this.getIntent().getStringExtra("iconS");


       //3: get user data from shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MarwaAdd.this);
        SPname = preferences.getString("Name", "");
        SPemail = preferences.getString("Email", "");
        SPweight = preferences.getString("Weight", "");


        //4: get interface items here
        Aname = findViewById(R.id.textView20);
        Aduration = findViewById(R.id.editText2);
     //   Adesc = findViewById(R.id.editText3);
        btnAddActivity = findViewById(R.id.button6);
        theIcon = findViewById(R.id.imageView10);

        //5: set the activity name
        Aname.setText(ActivityName);


        //6: show img from DB
        String theImage_URL = ActiIcon;


        ImageLoader imageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();

        imageLoader.get(theImage_URL, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  Toast.makeText(this., "big error" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {


                    theIcon.setImageBitmap(response.getBitmap());

                }
            }
        });


        //7: btn add activity
       BtnToAddActivity();
    }

    public void BtnToAddActivity(){
        btnAddActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        while(true)
                        {
                            if(Aduration.getText().toString().equals(""))
                            {
                                Toast.makeText(getApplicationContext(),"Enter Activity Duration",Toast.LENGTH_SHORT).show();
                                break;
                            }

                            int duration = Integer.valueOf(Aduration.getText().toString());
                            if(duration > 180)
                            {
                                Toast.makeText(getApplicationContext(),"Activity Duration cannot be more than 180 Minutes!",Toast.LENGTH_SHORT).show();
                                break;
                            }

                            WSAddActivity();
                            break;
                        }
                    }
                }
        );
    }

public void WSAddActivity(){
    StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PERSONAL_ACTIVITY_API_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            if(response.contains("success")) {

               finish();

            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),"failed to login",Toast.LENGTH_SHORT).show();
        }
    }){
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            params.put("Content-Type","application/x-www-form-urlencoded");
            params.put("Name", Aname.getText().toString());
            params.put("Day", date);
            params.put("Duration", Aduration.getText().toString());
           // params.put("description", Adesc.getText().toString());
            params.put("UserEmail", SPemail);
            params.put("Weight",  SPweight);
            params.put("Met",  ActiMet);
            params.put("Image",  ActiIcon);
            return params;
        }
    };

    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
}
}
