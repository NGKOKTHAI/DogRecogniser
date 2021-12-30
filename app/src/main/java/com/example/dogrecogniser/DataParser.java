package com.example.dogrecogniser;

import android.nfc.cardemulation.HostApduService;

import androidx.recyclerview.widget.LinearSmoothScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    //get place method store one place
    private HashMap<String,String> getPlace(JSONObject googlePlaceJson)
    {
        //create objects hashmap
        HashMap<String,String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";


        //fetching data
        try {
            if(!googlePlaceJson.isNull("name"))
            {
                placeName = googlePlaceJson.getString("name");
            }

            if(!googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            //putting data to hashmap
            googlePlaceMap.put("placeName",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longitude);
            googlePlaceMap.put("reference",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }

    //store a list of hashmap
    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String, String > placeMap = null;

        for(int i = 0; i<count;i++)
        {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    //pass jsondata and set to getplaces method
    public List<HashMap<String,String >> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }
}
