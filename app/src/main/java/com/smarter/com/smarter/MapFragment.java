package com.smarter.com.smarter;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.mapbox.mapboxsdk.MapboxAccountManager;
//import com.mapbox.mapboxsdk.annotations.Icon;
//import com.mapbox.mapboxsdk.annotations.MarkerOptions;
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapquest.mapping.maps.MapView;
//import com.mapquest.mapping.maps.MapboxMap;
//import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.smarter.tools.Datetools;
import com.smarter.tools.MapGeocode;
import com.smarter.tools.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.smarter.tools.Datetools.getQueryDate;
import static com.smarter.tools.Datetools.parse;
import static com.smarter.tools.Tool.BASE_URL;

/**
 * Created by kasal on 8/04/2018.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

//    private MapboxMap mMapboxMap;
//    private MapView mMapView;
    private GoogleMap mMap;
    private ToggleButton toggleButton;
    private Resident object;
    private FragmentActivity myContext;
    MapView mMapView;
//    private GoogleMap googleMap;
    private String availDate;
    private Double tmpUsage;
    private int tmphour;
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    Map<Pair<Double,Double>, Resident> map=new HashMap<Pair<Double,Double>, Resident>();

    public void setTmpUsage(double value)
    {
        tmpUsage = value;
    }

    public void setTmpHour(int value)
    {
        tmphour = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View newView = inflater.inflate(R.layout.fragment_map, container, false);
        toggleButton = newView.findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                for (Marker marker : mMarkerArray) {
                    Pair<Double,Double> pair = new Pair<>(marker.getPosition().latitude, marker.getPosition().longitude);
                    int resid = map.get(pair).getResid();
                    boolean status = toggleButton.isChecked();
                    if(status)
                        gerUsage("daily",marker, resid, null);
                    else
                        gerUsage("hourly",marker, resid, null);

                    mMapView.onResume();


                }
            }
        });
        mMapView = (MapView) newView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        toggleButton.setTextOff("Hourly");
        toggleButton.setText("Hourly");
        toggleButton.setTextOn("Daily");
        return newView;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mMapView = findViewById(R.id.mapquestMapView);
//        mMapView.onCreate(savedInstanceState);
//        MapboxAccountManager.start(getApplicationContext());
//        address = getArguments().getString("address", "");
//        WeatherFragment.WeatherUpdater wu = new WeatherFragment.WeatherUpdater();
//        wu.execute(Weather.getCity());
//        setContentView(R.layout.fragment_map);
        object = getArguments().getParcelable("resident");
    }


//    private void addMarker(MapboxMap mapboxMap, Pair<Double,Double> pair) {
//        LatLng location = new LatLng(pair.first, pair.second);
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(location);
//        markerOptions.title(map.get(pair).getFname() + " " + map.get(pair).getSname());
//        markerOptions.snippet("Welcome!");
//        mapboxMap.addMarker(markerOptions);
//    }

    @SuppressLint("StaticFieldLeak")
    private void gerAllResident()
    {
        AsyncTask<Void, Void, String> ayt = new AsyncTask<Void, Void, String>() {
            ProgressDialog progDailog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progDailog = new ProgressDialog(getActivity());
                progDailog.setMessage("Loading...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(false);
                progDailog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                String redURL = BASE_URL + "/Assignment/webresources/restws.resident/";
                try {
                    URL redRestful = new URL(redURL);
                    HttpURLConnection redConnection;
                    redConnection = (HttpURLConnection) redRestful.openConnection();
                    redConnection.setReadTimeout(10000);
                    redConnection.setConnectTimeout(15000);
                    redConnection.setRequestMethod("GET");
                    redConnection.setRequestProperty("Content-Type", "application/json");
                    redConnection.setRequestProperty("Accept", "application/json");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(redConnection.getInputStream()));

                    StringBuilder jsonBuilder = new StringBuilder(1024);
                    String tmp;
                    while ((tmp = reader.readLine()) != null)
                        jsonBuilder.append(tmp).append("\n");
                    reader.close();
                    JSONArray jsonRead = new JSONArray(jsonBuilder.toString());

                    for (int i = 0; i < jsonRead.length(); i++) {
                        Resident resident = readResident(jsonRead.getJSONObject(i));
                        String address = jsonRead.getJSONObject(i).getString("address");
                        MapGeocode mg = new MapGeocode(address);
                        Pair<Double, Double> pair = mg.getLatLon();
                        map.put(pair, resident);
                    }
                    redConnection.disconnect();


                    //get recent date

                    redURL = BASE_URL + "/Assignment/webresources/restws.usage/";
                    redRestful = new URL(redURL);
                    redConnection = (HttpURLConnection) redRestful.openConnection();
                    redConnection.setReadTimeout(10000);
                    redConnection.setConnectTimeout(15000);
                    redConnection.setRequestMethod("GET");
                    redConnection.setRequestProperty("Content-Type", "application/json");
                    redConnection.setRequestProperty("Accept", "application/json");

                    reader = new BufferedReader(
                            new InputStreamReader(redConnection.getInputStream()));

                    jsonBuilder = new StringBuilder(1024);
                    while ((tmp = reader.readLine()) != null)
                        jsonBuilder.append(tmp).append("\n");
                    reader.close();
                    jsonRead = new JSONArray(jsonBuilder.toString());

                    if (jsonRead.length() != 0) {
                        JSONObject jsonObject = jsonRead.getJSONObject(jsonRead.length() - 1);
                        availDate = getQueryDate(parse(jsonObject.getString("usagedate")));
                    }
                    redConnection.disconnect();

                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return "Done";


            }

            @Override
            protected void onPostExecute(String s) {

                // Add a marker in Sydney and move the camera

                boolean status = toggleButton.isChecked();

                for(final Pair<Double,Double> pair: map.keySet()) {
//                    mMapView.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(MapboxMap mapboxMap) {
//                            mMapboxMap = mapboxMap;
//                            mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pair.first, pair.second), 11));
//                            addMarker(mMapboxMap, pair);
//                        }
//                    });

                    LatLng des = new LatLng(pair.first, pair.second);
                    MarkerOptions mo = new MarkerOptions().position(des).title(map.get(pair).getFname() + " " + map.get(pair).getSname());

                    if(status) {
                        gerUsage("daily",null, map.get(pair).getResid(), mo);
                    }else {
                        gerUsage("hourly",null, map.get(pair).getResid(), mo);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(des,15));

                }
                progDailog.dismiss();
                super.onPostExecute(s);
            }
        };
        try {
            ayt.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void gerUsage(final String view, final Marker marker, final int resid, final MarkerOptions mo )
    {

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            ProgressDialog progDailog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progDailog = new ProgressDialog(getActivity());
                progDailog.setMessage("Loading...");
                progDailog.setIndeterminate(false);
                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDailog.setCancelable(false);
                progDailog.show();
            }
            @Override
            protected String doInBackground(Void... voids) {
                String redURL = BASE_URL + "/Assignment/webresources/restws.usage/getHourDailyUsage/"
                        + resid
                        + "/"
                        + availDate
                        + "/" + view;
                try {
                    URL redRestful = new URL(redURL);
                    HttpURLConnection redConnection;
                    redConnection = (HttpURLConnection) redRestful.openConnection();
                    redConnection.setReadTimeout(10000);
                    redConnection.setConnectTimeout(15000);
                    redConnection.setRequestMethod("GET");
                    redConnection.setRequestProperty("Content-Type", "application/json");
                    redConnection.setRequestProperty("Accept", "application/json");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(redConnection.getInputStream()));

                    StringBuilder jsonBuilder = new StringBuilder(1024);
                    String tmp;
                    while ((tmp = reader.readLine()) != null)
                        jsonBuilder.append(tmp).append("\n");
                    reader.close();
                    JSONArray jsonRead = new JSONArray(jsonBuilder.toString());
                    JSONObject jsonObject = null;
                    if(jsonRead.length() > 0) {
                        jsonObject = jsonRead.getJSONObject(jsonRead.length() - 1);
                        setTmpUsage(jsonObject.getDouble("usage"));

                        if (view.equals("hourly"))
                            setTmpHour(jsonObject.getInt("hours"));
                        else
                            setTmpHour(-1);
                    } else {
                        setTmpUsage(0);
                        setTmpHour(-1);
                    }

                    redConnection.disconnect();

                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "Done";


            }

            @Override
            protected void onPostExecute(String s) {

                if(mo != null) {
                    if (view.equals("Daily")) {
                        if (Tool.largerUsageDailyMap(getActivity(), tmpUsage)) {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                    }
                    else{
                        if (Tool.largerUsageHourlyMap(getActivity(), tmphour , tmpUsage)) {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                    }

                    Marker marker = mMap.addMarker(mo);
                    mMarkerArray.add(marker);

                    mMapView.onResume();
                }

                if (marker != null)
                    marker.setSnippet("Usage at " + availDate + "is :" + tmpUsage);

                progDailog.dismiss();
                super.onPostExecute(s);
            }
        };

        try {
            asyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public Resident readResident(JSONObject object) throws IOException, ParseException, JSONException {
        Integer resid = object.getInt("resid");
        String fname = object.getString("fname");
        String sname = object.getString("sname");
        Date dob = parse(object.getString("dob"));
        String address = object.getString("address");
        String postcode = object.getString("postcode");
        String email = object.getString("email");
        String mobile = object.getString("mobile");
        Integer number = object.getInt("number");
        String providerName = object.getString("providerName");
        return new Resident(resid, fname, sname, new java.sql.Date(dob.getTime()), address, postcode, email, mobile, number, providerName);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gerAllResident();
        mMap.setOnMarkerClickListener(this);
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Pair<Double,Double> pair = new Pair<>(marker.getPosition().latitude, marker.getPosition().longitude);
        int resid = map.get(pair).getResid();
        boolean status = toggleButton.isChecked();
        if(status)
            gerUsage("daily",marker, resid, null);
        else
            gerUsage("hourly",marker, resid, null);

        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
