package com.example.hasnaa.orangelabstask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Hasnaa on 14-08-2017.
 */
public class Utiles {




    public static ArrayList<String> parseJsonString (String response) throws JSONException {

        final String PHOTOS = "photos";
        final String PHOTO = "photo";
        final String ID = "id";
        final String SECRET = "secret";
        final String SERVER = "server";
        final String FARM = "farm";
        ArrayList<String> al = new ArrayList<>();
        JSONObject responseJson = new JSONObject(response);
        responseJson = responseJson.getJSONObject(PHOTOS);
        JSONArray photosArray = responseJson.getJSONArray(PHOTO);
        for (int i= 0 ;i<photosArray.length()&&i<40 ; i++) {
            URLDetails urlDetails= new URLDetails();
            JSONObject jsonObject =photosArray.getJSONObject(i);
            urlDetails.id=(jsonObject.getString(ID));
            urlDetails.secret=(jsonObject.getString(SECRET));
            urlDetails.serverId=(jsonObject.getString(SERVER));
            urlDetails.farmId=(jsonObject.getString(FARM));

            String s="https://farm"+urlDetails.farmId+".staticflickr.com/"+urlDetails.serverId+
                    "/"+urlDetails.id+"_"+ urlDetails.secret+"_z.jpg";
            al.add(s);
        }
        return al;
    }

}

