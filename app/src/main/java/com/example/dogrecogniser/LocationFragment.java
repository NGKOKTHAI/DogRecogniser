package com.example.dogrecogniser;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class LocationFragment extends Fragment {

    Spinner spType;
    Button btn_go,btn_mapType;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;


    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);


        spType = view.findViewById(R.id.sp_type);
        btn_go = view.findViewById(R.id.btn_go);
        btn_mapType = view.findViewById(R.id.btn_mapType);
        supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.google_map);


        //Initialize array of place type
        String[] placeTypeList = {"vet_clinic", "ngo_dog_shelter", "pet_grooming", "pet_hotel", "pet_shop"};

        //Initialize array of place name
        String[] placeNameList = {"Veterinary Clinic", "NGO Dog Shelter", "Pet Grooming", "Pet Hotel", "Pet Shop"};

        //set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, placeNameList));

        //initialise fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        //check permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted
            //call method
            getCurrentLocation();
        } else {
            //When permission denied
            //request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //initialise url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //Url
                        "?location=" + currentLat + "," + currentLong + //Location latitude and longitude
                        "&radius=5000" + //Nearby radius
                        "&types=" + placeTypeList[i] + //Place type
                        "&sensor=true" + //sensor
                        "&key=" + getResources().getString(R.string.google_map_key); //google map key

                //execute place task method to download json data
                new PlaceTask().excute(url);
            }
        });


        btn_mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeType();
            }
        });

        return view;
    }


    private void getCurrentLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(@NonNull Location location) {
                //when success
                if(location != null){
                    //when location is not null
                    //get current latitude
                    currentLat = location.getLatitude();
                    //get current longitude
                    currentLong = location.getLongitude();
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            //when map is ready
                            map = googleMap;
                            //zoom current location on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLong),10));
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //when permission granted
                //call method
                getCurrentLocation();
            }else{
                Toast.makeText(getActivity(),"Location Premission required!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                // initialise data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //execute parser task
            new ParserTask().execute(s);
        }

        public void excute(String url) {
        }
    }

    private String downloadUrl(String string) throws IOException {
        //initialise url
        URL url = new URL(string);
        //initialise connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //connect connection
        connection.connect();
        InputStream stream = connection.getInputStream();
        //initialise buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //initialize string builder
        StringBuilder builder = new StringBuilder();
        //initialise string variable
        String line = "";
        //use while loop
        while ((line = reader.readLine()) != null){
            //Append line
            builder.append(line);
        }
        //Get append data
        String data = builder .toString();
        //close reader
        reader.close();
        //return data
        return data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json class
            JsonParser jsonParser = new JsonParser();
            //initialise hash map list
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            try {
                //initialise json object
                object = new JSONObject(strings[0]);
                //parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //return map list
            return  mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //clear map
            map.clear();
            //use for loop
            for (int i=0; i<hashMaps.size(); i++){
                //initialise hash map
                HashMap<String,String> hashMapList = hashMaps.get(i);
                //get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                //get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));
                //get name
                String name = hashMapList.get("name");
                //concat latitude and longitude
                LatLng latlng = new LatLng(lat,lng);
                //initialise marker options
                MarkerOptions options = new MarkerOptions();
                //set position
                options.position(latlng);
                //set title
                options.title(name);
                //add marker on map
                map.addMarker(options);
            }

        }
    }


    public void changeType(){
        if (map.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }else
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}