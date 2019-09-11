package com.example.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    int locationNumber;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        goToLocation(new LatLng(location.getLatitude(),location.getLongitude()), null);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void goToLocation(LatLng latLng, String s) {
        Log.i("Location","goToLocation new " + s);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
        mMap.addMarker(new MarkerOptions().position(latLng).title(s));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        locationNumber = intent.getIntExtra("location",0);

        if (locationNumber > 0) {
            goToLocation(MainActivity.latLngs.get(locationNumber),MainActivity.places.get(locationNumber));
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String s = "No Address to save";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> place = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (place.size() > 0) {
                        s = "";
                        if (null != place.get(0).getFeatureName()) {
                            s += place.get(0).getFeatureName() + " ";
                        }
                        if (null != place.get(0).getThoroughfare()) {
                            s += place.get(0).getThoroughfare() + ", ";
                        }
                        if (null != place.get(0).getLocality()) {
                            s += place.get(0).getLocality() + ", ";
                        }
                        if (null != place.get(0).getAdminArea()) {
                            s += place.get(0).getAdminArea();
                        }
                        if (s.isEmpty()) {
                            s = "No Address to save";
                        } else {
                            MainActivity.places.add(s);
                            MainActivity.latLngs.add(latLng);
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),"Location saved",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMap.addMarker(new MarkerOptions().position(latLng).title(s));
            }
        });

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                goToLocation(new LatLng(location.getLatitude(),location.getLongitude()), null);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (0 == locationNumber) {
                goToLocation(new LatLng(location.getLatitude(),location.getLongitude()), "you");
            }
        } else {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else {
                Log.i("Location","Getting Last known loaction");
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (0 == locationNumber) {
                    goToLocation(new LatLng(location.getLatitude(),location.getLongitude()), "you");
                }
            }
        }

    }
}
