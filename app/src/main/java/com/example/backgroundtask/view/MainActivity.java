package com.example.backgroundtask.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.backgroundtask.R;
import com.example.backgroundtask.adapter.CityAdapter;
import com.example.backgroundtask.background.BatteryReceiver;
import com.example.backgroundtask.background.BookmarkCity;
import com.example.backgroundtask.database.DataHelperCityList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    public static GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    BatteryReceiver batteryReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        checkInterNetConnectivity();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    public void checkInterNetConnectivity() {

        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(MainActivity.this, "Connected to Internet", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this, "Not Connected to Internet", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        DataHelperCityList db = new DataHelperCityList(MainActivity.this);
        Cursor c;
        c = db.getCityData();
        if (c.getCount() == 0) {
            Toast.makeText(MainActivity.this, "No City Bookmarked Now", Toast.LENGTH_SHORT).show();

        }
        while (c.moveToNext()) {

            LatLng latLng = new LatLng(c.getDouble(1), c.getDouble(2));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(c.getString(3));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker storedCityMarker = mMap.addMarker(markerOptions);
            BookmarkCity.markerDelete.add(storedCityMarker);
        }
        mMap.setOnMapClickListener(latLng1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle(" Bookmark City?");
            builder.setMessage("Do you Want Bookmark City?");
            builder.setPositiveButton("yes", (dialog, which) -> {
                BookmarkCity bookmarkCity = new BookmarkCity(MainActivity.this);
                bookmarkCity.execute(latLng1);

            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });


    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this::onLocationChanged);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this::onLocationChanged);

        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        MenuItem menuItemBatteryPercentage = menu.findItem(R.id.menuItemBatteryPercentage);
        MenuItem menuItemBatteryPercentageText = menu.findItem(R.id.menuItemBatteryPercentageText);
        batteryReceiver = new BatteryReceiver(menuItemBatteryPercentage, menuItemBatteryPercentageText);
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemBookmarkedCity) {
            Intent intentGoCityScreen = new Intent(MainActivity.this, CityScreenActivity.class);
            startActivity(intentGoCityScreen);
        }
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        for (int i = 0; i < CityAdapter.deletedLatLong.size(); i++) {
            for (int j = 0; j < BookmarkCity.markerDelete.size(); j++) {
                if (BookmarkCity.markerDelete.get(j).getPosition().latitude == CityAdapter.deletedLatLong.get(i).latitude && BookmarkCity.markerDelete.get(j).getPosition().longitude == CityAdapter.deletedLatLong.get(i).longitude) {
                    BookmarkCity.markerDelete.get(j).remove();
                }
            }
        }

    }
}



