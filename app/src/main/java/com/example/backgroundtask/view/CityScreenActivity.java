package com.example.backgroundtask.view;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backgroundtask.R;
import com.example.backgroundtask.adapter.CityAdapter;
import com.example.backgroundtask.background.BatteryReceiver;
import com.example.backgroundtask.database.DataHelperCityList;
import com.example.backgroundtask.model.CityDetails;

import java.util.ArrayList;

public class CityScreenActivity extends Activity {
    private RecyclerView rvCityList;
    DataHelperCityList db=new DataHelperCityList(CityScreenActivity.this);
    CityAdapter adapter;
    BatteryReceiver batteryReceiver;
    private final ArrayList<CityDetails> city = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_screen);
        rvCityList = findViewById(R.id.rvCityList);
        createCityDataList();
        setAdapterRecyclerView();
    }

    public void setAdapterRecyclerView() {

        adapter = new CityAdapter(city,CityScreenActivity.this);
        rvCityList.setAdapter(adapter);
        rvCityList.addItemDecoration(new DividerItemDecoration(CityScreenActivity.this, DividerItemDecoration.VERTICAL));
        rvCityList.setLayoutManager(new LinearLayoutManager(CityScreenActivity.this));

    }

    public void createCityDataList() {
        Cursor getData=db.getCityData();
        if (getData.getCount() == 0) {
            Toast.makeText(CityScreenActivity.this, "No City Bookmarked Now", Toast.LENGTH_SHORT).show();

        }
        while (getData.moveToNext()) {
            Log.d("CityNameCityScreen",getData.getString(3));

            city.add(new CityDetails(getData.getInt(0), getData.getString(3), getData.getDouble(5), getData.getInt(6), getData.getDouble(1), getData.getDouble(2), getData.getString(4), getData.getString(7)));
        }
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