package com.smarter.tools;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smarter.com.smarter.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kasal on 28/03/2018.
 */

public class Weather {
    private static final String OPEN_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat=";
    public static JSONObject getJSON (Context context, String lat, String lon) {
        URL url;

        JSONObject data = null;
        try {
            url = new URL(OPEN_WEATHER_URL + lat + "&lon=" + lon +  "&units=metric&appid=" + context.getString(R.string.open_weather_maps_app_id));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(5000);
            connection.setConnectTimeout(3000);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp;
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getCity(){
        return "Melbourne, AU";
    }


    public static double getWeather(final String userAddress, final Context context)
    {
        double d = 0;

        MapGeocode mg = new MapGeocode(userAddress);
        Pair<Double,Double> pair = mg.getLatLon();
        final JSONObject json = Weather.getJSON(context, pair.first.toString(), pair.second.toString());
        if(json == null){
            Toast.makeText(context,context.getString(R.string.place_not_found),Toast.LENGTH_LONG).show();

        } else try {
            JSONObject main = json.getJSONObject("main");
            d = main.getDouble("temp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return d;
    }

    void setCity(String city){
//
    }


}
