package com.example.backgroundtask.background;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.backgroundtask.database.DataHelperCityList;
import com.example.backgroundtask.view.MainActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookmarkCity extends AsyncTask<LatLng, String, Void> {
    RequestQueue requestQueue;
    LatLng latLngOfClickLocation;
    String cityName = "";
    ProgressDialog pdWaiting;
     public  static ArrayList<Marker> markerDelete=new ArrayList<>();

    Context ctx;

    public BookmarkCity(Context ct) {
        ctx = ct;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pdWaiting = new ProgressDialog(ctx);
        pdWaiting.setCanceledOnTouchOutside(false);
        pdWaiting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdWaiting.setCancelable(true);
        pdWaiting.setMax(10);
        pdWaiting.setProgress(0);
        pdWaiting.setMessage("Please Wait ....");
        pdWaiting.show();


    }

    @Override
    protected Void doInBackground(LatLng... latLngs) {
        latLngOfClickLocation = latLngs[0];
        requestQueue = Volley.newRequestQueue(ctx);
        String tempUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latLngOfClickLocation.latitude + "&lon=" + latLngOfClickLocation.longitude + "&appid=" + "fae7190d7e6433ec3a45285ffcf55c86";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, tempUrl, null, response -> {
                    Log.d("response get", String.valueOf(response));
                    String output = "";
                    try {
                        cityName += response.getString("name");
                        Log.d("City", cityName);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLngOfClickLocation);
                        markerOptions.title(cityName);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        Marker cityAdd=MainActivity.mMap.addMarker(markerOptions);
                        markerDelete.add(cityAdd);
                        JSONArray jsonArrayWeather = response.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String weatherDescription = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = response.getJSONObject("main");
                        double temperature = jsonObjectMain.getDouble("temp") - 273.15;
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = response.getJSONObject("wind");
                        String windSpeed = jsonObjectWind.getString("speed");
                        output += "City" + cityName + "Weather Description" + weatherDescription + "temperature" + temperature + "C" + "humidity" + humidity + "%" + " Wind Speed" + windSpeed + "m/s";
                        Log.d("output", output);
                        DataHelperCityList db = new DataHelperCityList(ctx);
                        Boolean temp = db.insertData(cityName, latLngOfClickLocation.latitude, latLngOfClickLocation.longitude, weatherDescription, temperature, humidity, windSpeed);
                        Log.d("insert Data", temp.toString());

                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                }, error -> {

                });

        requestQueue.add(jsonObjectRequest);

        return null;
    }


    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        pdWaiting.setProgress(10);
        pdWaiting.dismiss();
    }
}
