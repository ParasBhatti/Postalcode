package com.example.ricky.postalcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    TextView out;
    Button search;
    double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        out=(TextView)findViewById(R.id.output);
        search=(Button)findViewById(R.id.find);

        PlaceAutocompleteFragment placeAutocompleteFragment= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                lat=place.getLatLng().latitude;
                lng=place.getLatLng().longitude;
                Toast.makeText(MainActivity.this, lat+" "+lng, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> data=new HashMap<>();
                data.put("latlng",lat+","+lng);
                    data.put("key","AIzaSyDELpqMi27VwVMB44JliiQG3wSDAYEuG_c");

                Api api=ApiClient.apiclient().create(Api.class);
                Call<Map> call=api.placedata(data);

                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {

                        Gson gson=new Gson();
                        String json=gson.toJson(response.body());

                        try {
                            JSONObject jsonObject=new JSONObject(json);
                            String status=jsonObject.getString("status");

                            if (status.equalsIgnoreCase("OK")){
                                JSONArray array=jsonObject.getJSONArray("results");
                                //out.setText(array.length());

                                JSONArray array1=array.getJSONObject(0).getJSONArray("address_components");


                               String data=array1.getJSONObject(array1.length()-1).getString("long_name");

                                Log.d( "onResponse: ", data);
                                out.setText(data);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {

                    }
                });
            }
        });
    }
}
