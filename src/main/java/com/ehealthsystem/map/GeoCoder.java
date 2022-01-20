package com.ehealthsystem.map;

import com.ehealthsystem.database.Database;
import com.ehealthsystem.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.RequestDeniedException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GeoCoder {
    /**
     * Verifies an address
     * @param address consisting of street and optionally including number
     * @param zip the zip code
     * @return if address was found: the entered address, properly formatted
     * @throws IOException
     * @throws InterruptedException
     * @throws ApiException
     */
    public static String geocode(String address, String zip) throws IOException, InterruptedException, ApiException {
        // set API Key
        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyCUFsJZUQjbl0_0o8DAhQzhMOvxhftI6KQ").build();

        // geocode address + zip
        if(address.isBlank() && zip.isBlank()) {
            return null;
        }

        GeocodingResult[] results = GeocodingApi.geocode(context,address + "," + zip).language("de-DE").await();
        String formattedAddress = results[0].formattedAddress;
        return formattedAddress;
    }

    public static LatLng geocodeToLatLng(String address) throws IOException, InterruptedException, ApiException {
        // set API Key
        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyCUFsJZUQjbl0_0o8DAhQzhMOvxhftI6KQ").build();
        GeocodingResult[] results = GeocodingApi.geocode(context,address).language("de-DE").await();
        return results[0].geometry.location;
    }
}

// https://maps.googleapis.com/maps/api/distancematrix/json?origins=KarbenerWeg61184Karben&destinations=Gartenstra%C3%9Fe61184Karben&mode=bicycling&language=de-DE&key=AIzaSyCUFsJZUQjbl0_0o8DAhQzhMOvxhftI6KQ
// https://maps.googleapis.com/maps/api/distancematrix/json?origins=40.6655101%2C-73.89188969999998&destinations=40.659569%2C-73.933783&key=AIzaSyCUFsJZUQjbl0_0o8DAhQzhMOvxhftI6KQ