package com.cs354.iitp.iitpseer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EntryExit extends AppCompatActivity {

    TextView textView_name, textView_phone, textView_email, textView_building;
    Button button_entry, button_exit;

    private static final String LOG_TAG = EntryExit.class.getSimpleName();

    NetworkImageView imageView;

    private String name, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_exit);


        textView_name = findViewById(R.id.textView_name);
        textView_phone = findViewById(R.id.textView_phone);
        textView_email = findViewById(R.id.textView_email);
        button_entry = findViewById(R.id.button_entry);
        button_exit = findViewById(R.id.button_exit);
        imageView = findViewById(R.id.imageView_photo);
        textView_building = findViewById(R.id.textView_building);

//        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        assert extras != null;
        final String message = extras.getString("EXTRA_MESSAGE");

        final String building = extras.getString("EXTRA_MESSAGE2");

        final String building_name = extras.getString("EXTRA_MESSAGE3");

        getData(message, building_name);

        button_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEntry(message, building, name);
            }
        });

        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeExit(message, building, name);
            }
        });

    }

    private void makeEntry(String id_name, String building, String fullName) {

        Log.d(LOG_TAG, "id_name " + id_name);

        Map<String, String> params = new HashMap<>();
        params.put("ID", id_name);
        params.put("BuildingNo", building);
        params.put("FullName", fullName);

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, Constants.ENTRY_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onBackPressed();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error", error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });


        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        queue.add(jsonObjectRequest);
    }

    private void makeExit(String id_name, String building, String fullName) {

        Log.d(LOG_TAG, "id_name " + id_name);

        Map<String, String> params = new HashMap<>();
        params.put("ID", id_name);
        params.put("BuildingNo", building);
        params.put("FullName", fullName);

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, Constants.EXIT_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(),response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onBackPressed();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response Error", error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });


        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        queue.add(jsonObjectRequest);
    }

    private void getData(String id, final String building_name) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constants.PEOPLE_URL + "?id="+ id, new JSONObject(new HashMap<>()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            name = jsonObject.getString("FullName");
                            phone = jsonObject.getString("Phone");
                            email = jsonObject.getString("Email");
                            textView_name.setText(name);
                            textView_phone.setText( phone);
                            textView_email.setText(email);
                            textView_building.setText(building_name);
                            ImageLoader mImageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
                            NetworkImageView avatar = findViewById(R.id.imageView_photo);
                            avatar.setImageUrl(Constants.ROOT_URL+"/"+jsonObject.getString("ImageURL"), mImageLoader);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Token " + Constants.Token);
                return headers;
            }
        };


        RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        queue.add(jsonObjectRequest);

    }

    public void onBackPressed() {
        this.startActivity(new Intent(EntryExit.this, MainActivity.class));
    }
}
