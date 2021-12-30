package com.example.dogrecogniser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


import java.io.IOException;
import java.util.List;

public class Map_View extends  FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private com.google.android.gms.maps.GoogleMap mMap;
    private GoogleApiClient client;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    private LocationRequest locationRequest;
    int PROXIMITY_RADIUS = 1000;
    public static final int REQUEST_LOCATION_CODE = 99;
    double latitude, longitude;
    EditText ed_searchPlace;
    Button btn_find, btn_vet, btn_ngo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map__view);

        ed_searchPlace = findViewById(R.id.ed_searchPlace);
        btn_find = findViewById(R.id.btn_find);
        btn_vet = findViewById(R.id.btn_vet);
        btn_ngo = findViewById(R.id.btn_ngo);

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed_searchPlace = (EditText)findViewById(R.id.ed_searchPlace);
                String location = ed_searchPlace.getText().toString();
                List<Address> addressList = null;

                if(!location.equals("")){
                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {
                        addressList = geocoder.getFromLocationName(location,5);

                        if(addressList != null)
                        {
                            for(int i = 0; i < addressList.size(); i++){
                                Address myAddress = addressList.get(i);
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        btn_vet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                String veterinary_clinic  = "Veterinary Clinic";
                String url = getUrl(latitude, longitude, veterinary_clinic);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Map_View.this, "Showing nearby veterinary clinics.", Toast.LENGTH_LONG).show();
            }
        });

        btn_ngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                String ngo_dog_shelter  = "NGO Dog Shelter";
                String url = getUrl(latitude, longitude, ngo_dog_shelter);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(Map_View.this, "Showing nearby NGO Dog Shelters.", Toast.LENGTH_LONG).show();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }


        //obtain the supportmapfragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
                }
        }
    }


    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized  void buildGoogleApiClient(){
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyDAp_y96Iaq_xQ2tsgCtyx_c1pNos2ADTs");

        return googlePlaceUrl.toString();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(client,locationRequest,  this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
