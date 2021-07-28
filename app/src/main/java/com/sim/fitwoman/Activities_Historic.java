package com.sim.fitwoman;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sim.fitwoman.R;
import com.sim.fitwoman.adapter.ActivityHistoricAdapter;
import com.sim.fitwoman.model.MActivityHistoric;
import com.sim.fitwoman.utils.WSadressIP;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activities_Historic extends AppCompatActivity {
    ListView listView;
    List<MActivityHistoric> lstccM;
    List<MActivityHistoric> lstcs;
    private String PERSONAL_ACTIVITIES_API_URL = "http://10.0.2.2:8012/fitness/api/get-personal-activities.php";
    private String IMAGES_DIR_URL = "http://10.0.2.2:8012/fitness/images/workouts/";
    String SPname, SPemail;
    EditText searchingActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities__historic);

        //1: toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar5);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.m31);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TextView nomeals = (TextView) findViewById(R.id.nomeals);


        //2: list view
        listView = (ListView) findViewById(R.id.activities_historic_listview);
        lstccM = new ArrayList<>();

        //get searched text
        searchingActivity = findViewById(R.id.editTexti);
        searchingActivity.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {


                        //loadActivities();
                        searchByDate();

                        //on item list click move to add activity

                        return true;
                    }
                }
        );

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SPname = preferences.getString("Name", "");
        SPemail = preferences.getString("Email", "");
        loadActivities();
        listView.setEmptyView(nomeals);
        //3: get data from shared preferences
    }
    
    private void loadActivities() {

        JSONObject postData = new JSONObject();
        Map<String, String> params = new HashMap<>();
        params.put("UserEmail", SPemail);

        postData = new JSONObject(params);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, PERSONAL_ACTIVITIES_API_URL,postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = response.getJSONArray("activities");

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                lstccM.add(new MActivityHistoric(
                                        product.getInt("PersonalActivityId"),
                                        product.getString("Name"),

                                        product.getString("CreatedDate"),

                                        product.getString("Duration"),

                                        product.getString("BurnedCalories")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            ActivityHistoricAdapter adapter = new  ActivityHistoricAdapter(getApplicationContext(), R.layout.activities_historic_row,lstccM);
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
//                params.put("emailUser", SPemail);
//
//                return params;
//            }
        };;

        //adding our stringrequest to queue
        Volley.newRequestQueue(Activities_Historic.this).add(stringRequest);
    }


    public void searchByDate(){
        String s = searchingActivity.getText().toString();

        if(s.length() > 0) {
            lstcs = new ArrayList<>();
            for (MActivityHistoric i : lstccM) {
                if (i.getDay().toUpperCase().contains(s.toUpperCase())) {
                    lstcs.add(new MActivityHistoric(i.getId(), i.getName(),i.getDay(),i.getDuration(), i.getBurnedCalories()));
                }
            }

          ActivityHistoricAdapter adapter = new   ActivityHistoricAdapter(Activities_Historic.this, R.layout.activities_historic_row, lstcs);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }else{
            ActivityHistoricAdapter adapter = new   ActivityHistoricAdapter(Activities_Historic.this, R.layout.activities_historic_row, lstccM);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }

    }
}
