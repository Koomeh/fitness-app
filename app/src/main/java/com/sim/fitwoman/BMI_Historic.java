package com.sim.fitwoman;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sim.fitwoman.R;
import com.sim.fitwoman.adapter.BMIHistoricAdapter;
import com.sim.fitwoman.model.MBMIHistoric;
import com.sim.fitwoman.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMI_Historic extends AppCompatActivity {
    ListView listView;
    List<MBMIHistoric> lstccM;
    String SPname, SPemail, SPweight ,SPheight, SBMI;
    private String BMI_HISTORY_API_URL = "http://10.0.2.2:8012/fitness/api/get-bmi-history.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi__historic);

        //1: toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar4);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.m31);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //2: set list view
        listView = (ListView) findViewById(R.id.bmi_historic_listview);
        lstccM = new ArrayList<>();

        //3: get data from shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SPname = preferences.getString("Name", "");
        SPemail = preferences.getString("Email", "");
        SPweight = preferences.getString("Weight", "");
        SPheight = preferences.getString("Height", "");
        SBMI = preferences.getString("BMI", "");

        loadActivities();
    }
    private void loadActivities() {
        JSONObject postData = new JSONObject();
        Map<String, String> params = new HashMap<>();
        params.put("UserEmail", SPemail);
        postData = new JSONObject(params);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, BMI_HISTORY_API_URL,postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = response.getJSONArray("histories");
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String bmi = preferences.getString("BMI", "");
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                lstccM.add(new MBMIHistoric(
                                    product.getString("CreatedDate"),
                                    product.getString("Weight"),
                                    bmi,
                                    product.getString("BMI")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            BMIHistoricAdapter adapter = new BMIHistoricAdapter(getApplicationContext(), R.layout.bmi_historic_row,lstccM);
                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//
//
//
//                params.put("emailUser", SPemail);
//
//                return params;
//            }
        };;

        //adding our stringrequest to queue
        Volley.newRequestQueue(BMI_Historic.this).add(stringRequest);
    }
}
