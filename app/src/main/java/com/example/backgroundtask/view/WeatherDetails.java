package com.example.backgroundtask.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backgroundtask.R;
import com.example.backgroundtask.background.BatteryReceiver;
import com.example.backgroundtask.database.DataHelperCityList;
import com.example.backgroundtask.model.CityDetails;

public class WeatherDetails extends Activity {

    int id;
    private final DataHelperCityList DB = new DataHelperCityList(WeatherDetails.this);
    private Cursor c;
    CityDetails cityDetails;
    BatteryReceiver batteryReceiver;

    TextView tvCity;
    TextView tvLat;
    TextView tvLong;
    TextView tvWeather;
    TextView tvTemperature;
    TextView tvHumidity;
    TextView tvWindSpeed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        init();

        Intent i = getIntent();
        id = i.getExtras().getInt("id");
        c = DB.getDateCityFromID(id);
        createDataOfWeather();
        setDataOfWeather();
    }

    private void init() {
        tvCity = findViewById(R.id.tvCity);
        tvLat=findViewById(R.id.tvLatitude);
        tvLong=findViewById(R.id.tvLongitude);
        tvWeather=findViewById(R.id.tvWeather);
        tvHumidity=findViewById(R.id.tvHumidity);
        tvWindSpeed=findViewById(R.id.tvWindSpeed);
        tvTemperature=findViewById(R.id.tvTemperature);
    }


    private void createDataOfWeather() {

        if (c.getCount() == 0) {
            Toast.makeText(WeatherDetails.this, "No City Right Now", Toast.LENGTH_SHORT).show();

        }
        while (c.moveToNext()) {
            cityDetails = new CityDetails(c.getInt(0), c.getString(3), c.getDouble(5), c.getInt(6), c.getDouble(1), c.getDouble(2), c.getString(4), c.getString(7));
        }

    }
    @SuppressLint("DefaultLocale")
    private void setDataOfWeather() {
         tvCity.setText(cityDetails.getCityName());
         tvLat.setText(String.format("%.2f",cityDetails.getLatitude()));
         tvLong.setText(String.format("%.2f",cityDetails.getLongitude()));
         tvWeather.setText(cityDetails.getWeatherDescription());
         tvTemperature.setText(String.format("%.2f",cityDetails.getTemperature()));
         tvHumidity.setText(String.valueOf(cityDetails.getHumidity()));
         tvWindSpeed.setText(cityDetails.getWindSpeed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_all_screen, menu);
        MenuItem menuItemBatteryPercentage=menu.findItem(R.id.menuItemBatteryPercentage);
        MenuItem menuItemBatteryPercentageText=menu.findItem(R.id.menuItemBatteryPercentageText);
        batteryReceiver=new BatteryReceiver(menuItemBatteryPercentage,menuItemBatteryPercentageText);
        registerReceiver(batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return true;
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }
}
