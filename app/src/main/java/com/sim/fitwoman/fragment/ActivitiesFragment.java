package com.sim.fitwoman.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sim.fitwoman.BMI_Historic;
import com.sim.fitwoman.Home;
import com.sim.fitwoman.MarwaFirstAddActivity;
import com.sim.fitwoman.R;
import com.sim.fitwoman.adapter.ActivityAdapter;

import com.sim.fitwoman.adapter.BMIHistoricAdapter;
import com.sim.fitwoman.model.MActivity;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sim.fitwoman.model.MBMIHistoric;
import com.sim.fitwoman.service.MySingleton;
import com.sim.fitwoman.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivitiesFragment extends Fragment {
    Button btnAdd;
    ImageView imgAdd;
    ListView lv;
    List<MActivity> lstcc;
    private static final String URL_Activities = "http://"+ WSadressIP.WSIP+"/FitWomanServices/MgetActivityByUserDay.php";
    private static  String GET_PERSONAL_ACTIVITIES_API_URL = "http://10.0.2.2:8012/fitness/api/get-personal-activities.php";
	private static  String DELETE_PERSONAL_ACTIVITY_API_URL = "http://10.0.2.2:8012/fitness/api/delete-activity.php";
    private String ACTIVITIES_DIR_URL = "http://10.0.2.2:8012/fitness/images/activities/";
    TextView TodayDay;
    String SPname, SPemail, SPweight;
    Integer TotalCalories = 0;
    Integer TotalActivities = 0;
    Integer TotalMinutes = 0;
    TextView tvCalories, tvActivities, tvMinutes, nomeals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_activities, container, false);
        //set the day
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        TodayDay = v.findViewById(R.id.MtextView11);
        TodayDay.setText("Today, "+date);

        //set activities or no
        nomeals = v.findViewById(R.id.textView47);

        //set total(calories,activities,minutes)
        tvActivities = v.findViewById(R.id.MtextView4);
        tvCalories = v.findViewById(R.id.MtextView6);
        tvMinutes = v.findViewById(R.id.textView15);


        // List view
        lv = v.findViewById(R.id.MlistView1);
        lstcc = new ArrayList<>();
        loadActivities();

        //Add activity
        imgAdd = v.findViewById(R.id.MimageView7);
        imgAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), MarwaFirstAddActivity.class);
                        //  i.putExtra("userName", SPname);
                        //i.putExtra("userEmail", SPemail);
                        //i.putExtra("userWeight", SPweight);
                        startActivity(i);
                    }
                }
        );
        ///// set no activities empty when list is full
        lv.setEmptyView(nomeals);
        ///////


        //DELETE item by position => LISTENER CLICK
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                MActivity data= (MActivity) arg0.getItemAtPosition(arg2);
                final int idS = data.getId();
                final int burnedCaloriesS = data.getBurnedCalories();
                //Toast.makeText(getContext(),String.valueOf(idS),Toast.LENGTH_SHORT).show();

                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivityNonNull());
                aBuilder.setMessage(" Do you want to delete this Actitivy ?")
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteActivity(idS);


                               // lstcc.clear();

                               // loadActivities();
                                Intent i = new Intent(getContext(), Home.class);
                                i.putExtra("okok", "ok");

                                startActivity(i);



                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert =  aBuilder.create();
                alert.setTitle("Delete Activity");
                alert.show();

            }
        });
        return v;
    }

    protected FragmentActivity getActivityNonNull() {
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("null returned from getActivity()");
        }
    }

    private void loadActivities() {

        JSONObject postData = new JSONObject();
        Map<String, String> params = new HashMap<>();
        params.put("UserEmail", SPemail);

        postData = new JSONObject(params);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, GET_PERSONAL_ACTIVITIES_API_URL, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting the string to json array object
                            //JSONArray array = new JSONArray(response);
                            JSONArray array = response.getJSONArray("activities");
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                //adding the product to product list
                                lstcc.add(new MActivity(
                                        product.getInt("PersonalActivityId"),
                                        product.getString("Name"),
                                        product.getString("Duration"),
                                        product.getInt("BurnedCalories"),
                                        ACTIVITIES_DIR_URL + product.getString("Image"),
                                        product.getString("Description")

                                ));
                                TotalCalories = TotalCalories + Integer.valueOf(product.getInt("BurnedCalories"));
                                TotalMinutes = TotalMinutes + Integer.valueOf(product.getInt("Duration"));
                            }
                            TotalActivities = array.length();
                            tvActivities.setText(String.valueOf(TotalActivities));
                            tvCalories.setText(String.valueOf(TotalCalories));
                            tvMinutes.setText(String.valueOf(TotalMinutes));
                            //creating adapter object and setting it to recyclerview
                            ActivityAdapter adapter = new ActivityAdapter(getContext(), R.layout.activities_row, lstcc);
                            adapter.notifyDataSetChanged();
                            lv.setAdapter(adapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                String datee = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                params.put("UserEmail", SPemail);
//                params.put("day", datee);
//                return params;
//            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get data from Shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SPname = preferences.getString("Name", "");
        SPemail = preferences.getString("Email", "");
        SPweight = preferences.getString("Weight", "");
        if (getArguments() != null) {

        }
    }

    private void deleteActivity(final int id) {
        JSONObject postData = new JSONObject();
        Map<String, String> params = new HashMap<>();
        params.put("PersonalActivityId", String.valueOf(id));
        postData = new JSONObject(params);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, DELETE_PERSONAL_ACTIVITY_API_URL,postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //converting the string to json array object
                            String errorfound = response.getString("errorfound");
                            String msg = response.getString("message");
                            //JSONArray array = response.getJSONArray("histories");


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
        };;

        //adding our stringrequest to queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
        //MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}