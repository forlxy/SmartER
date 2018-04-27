package com.smarter.tools;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.util.Pair;

import com.smarter.com.smarter.MainActivity;
import com.smarter.com.smarter.Resident;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kasal on 8/04/2018.
 */

public class MapGeocode {

    public String location = null;

    public MapGeocode(String location)
    {
        this.location = location;
    }

    public static Pair<Double,Double> readLatLon(JSONObject json) throws JSONException {
        Double lat = -37.815018;
        Double lon = 144.946014;


        lat = json.getJSONArray("results").getJSONObject(0)
                .getJSONArray("locations").getJSONObject(0).getJSONObject("latLng").getDouble("lat");
        lon = json.getJSONArray("results").getJSONObject(0)
                .getJSONArray("locations").getJSONObject(0).getJSONObject("latLng").getDouble("lng");

        return new Pair<>(lat, lon);
    }

//    public static Pair<Double,Double> readLatLon(JsonReader reader) throws IOException, ParseException {
//        Double lat = -37.815018;
//        Double lon = 144.946014;
//
//        reader.beginObject();
//        while (reader.hasNext()) {
//            String name = reader.nextName();
//            if (name.equals("results")) {
//                reader.beginArray();
//                reader.beginObject();
//                while (reader.hasNext()) {
//                    name = reader.nextName();
//                    if (name.equals("locations")) {
//                        reader.beginArray();
//                        if (!reader.hasNext()) {
//                            reader.endArray();
//                            break;
//                        }
//                        reader.beginObject();
//                        while (reader.hasNext()) {
//                            name = reader.nextName();
//                            if (name.equals("latLng")) {
//                                reader.beginObject();
//                                while (reader.hasNext()) {
//                                    name = reader.nextName();
//                                    if (name.equals("lat")) {
//                                        lat = reader.nextDouble();
//                                    } else if (name.equals("lng")) {
//                                        lon = reader.nextDouble();
//                                    } else {
//                                        reader.skipValue();
//                                    }
//                                }
//                                reader.endObject();
//                            } else {
//                                reader.skipValue();
//                            }
//
//                        }
//                        reader.endObject();
//                        reader.endArray();
//                    } else {
//                        reader.skipValue();
//                    }
//                }
//                reader.endObject();
//                reader.endArray();
//            } else {
//                reader.skipValue();
//            }
//        }
//        reader.endObject();
//        return new Pair<Double,Double>(lat,lon);
//    }

    public Pair<Double, Double> getLatLon() {
        String url = "http://www.mapquestapi.com/geocoding/v1/address?key=nhGuvc5xxAwD4mWTMPLzAUGx6tIvGydb&location=" + location;
        Pair<Double,Double> pair = null;
        try {
            URL restful = new URL(url);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) restful.openConnection();

            connection.setReadTimeout(5000);
            connection.setConnectTimeout(3000);

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == 200) {

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp;
                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();
                JSONObject data = new JSONObject(json.toString());

                pair = readLatLon(data);

            } else {
                Log.e("Login","Connect Error: Code is " + connection.getResponseCode());
                pair = new Pair<>(-37.815018, 144.946014);
            }
            connection.disconnect();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pair;
    }

//    public Pair<Double, Double> getLatLon() {
//        String url = "http://www.mapquestapi.com/geocoding/v1/address?key=nhGuvc5xxAwD4mWTMPLzAUGx6tIvGydb&location=" + location;
//        Pair<Double,Double> pair = null;
//        try {
//            URL restful = new URL(url);
//            HttpURLConnection connection = null;
//            connection = (HttpURLConnection) restful.openConnection();
//
//            connection.setReadTimeout(5000);
//            connection.setConnectTimeout(3000);
//
//            connection.setRequestMethod("GET");
//            //                                connection.setRequestProperty("User-Agent", "SmartER");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Accept", "application/json");
//
//            if (connection.getResponseCode() == 200) {
//
//                InputStream responseBody = connection.getInputStream();
//                InputStreamReader responseBodyReader =
//                        new InputStreamReader(responseBody, "UTF-8");
//                JsonReader jsonReader = new JsonReader(responseBodyReader);
//                //                                    jsonReader.beginObject(); // Start processing the JSON object
//
//                pair = readLatLon(jsonReader);
//
//                jsonReader.close();
//            } else {
//                Log.e("Login","Connect Error: Code is " + connection.getResponseCode());
//                pair = new Pair<>(-37.815018, 144.946014);
//            }
//            connection.disconnect();
//        } catch (ProtocolException e1) {
//            e1.printStackTrace();
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        } catch (ParseException e1) {
//            e1.printStackTrace();
//        }
//        return pair;
//    }

}
