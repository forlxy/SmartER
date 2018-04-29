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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
//    private ToggleButton toggleButton;
    private Resident object;
    private FragmentActivity myContext;
    MapView mMapView;
    private Spinner sp_map_viewtype;
//    private GoogleMap googleMap;
    private String availDate;
    ProgressDialog progDailog;
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    Map<Pair<Double,Double>, MapResidUsage> map=new HashMap<Pair<Double,Double>, MapResidUsage>();

    public class MapResidUsage{
        Resident resident;
        double usage;
        int hour;

        public MapResidUsage(Resident rd){
            resident = rd;
        }

        public double getUsage() {
            return usage;
        }

        public int getHour() {
            return hour;
        }

        public Resident getResident() {
            return resident;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setResident(Resident resident) {
            this.resident = resident;
        }

        public void setUsage(double usage) {
            this.usage = usage;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View newView = inflater.inflate(R.layout.fragment_map, container, false);
        sp_map_viewtype = newView.findViewById(R.id.sp_map_viewtype);


//        List<String> list = Arrays.asList(getResources().getStringArray(R.array.chart_array));
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_white, list);
//
//        sp_map_viewtype.setAdapter(adapter);

        sp_map_viewtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Marker marker : mMarkerArray) {
                    Pair<Double,Double> pair = new Pair<>(marker.getPosition().latitude, marker.getPosition().longitude);
                    if(position == 1)
                        gerUsage(pair, "daily",marker, null);
                    else
                        gerUsage(pair, "hourly",marker, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMapView = (MapView) newView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        if(mMapView != null)
            mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return newView;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            object = savedInstanceState.getParcelable("resident");
        else
            object = ((MainActivity)getActivity()).getObject();
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

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
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
                        map.put(pair, new MapResidUsage(resident));
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

                int pos = sp_map_viewtype.getSelectedItemPosition();
                for(final Pair<Double,Double> pair: map.keySet()) {
                    LatLng des = new LatLng(pair.first, pair.second);
                    MarkerOptions mo = new MarkerOptions().position(des).title(map.get(pair).getResident().getFname() + " " + map.get(pair).getResident().getSname());

                    if(pos == 1) {
                        gerUsage(pair,"daily",null, mo);
                    }else {
                        gerUsage(pair,"hourly",null, mo);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(des,15));

                }
                dismissProgressDialog();
                super.onPostExecute(s);
            }
        };
        ayt.execute();
    }

    private void showProgressDialog() {
        if (progDailog == null) {
            progDailog = new ProgressDialog(getActivity());
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }
        progDailog.show();
    }

    private void dismissProgressDialog() {
        if (progDailog != null && progDailog.isShowing()) {
            progDailog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void gerUsage(final Pair<Double,Double> pair, final String view, final Marker marker, final MarkerOptions mo )
    {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            int resid = map.get(pair).getResident().getResid();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(Void... voids) {
                String redURL = String.format("%s/Assignment/webresources/restws.usage/getHourDailyUsage/%d/%s/%s", BASE_URL, resid, availDate, view);
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
                    JSONObject jsonObject;
                    if(jsonRead.length() > 0) {
                        jsonObject = jsonRead.getJSONObject(jsonRead.length() - 1);
                        map.get(pair).setUsage(jsonObject.getDouble("usage"));

                        if (view.equals("hourly"))
                            map.get(pair).setHour(jsonObject.getInt("hours"));
                        else
                            map.get(pair).setHour(-1);
                    } else {
                        map.get(pair).setUsage(0);
                        map.get(pair).setHour(-1);
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
                    if (view.equals("daily")) {
                        if (Tool.largerUsageDailyMap(getActivity(), map.get(pair).getUsage())) {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                    }
                    else{
                        if (Tool.largerUsageHourlyMap(getActivity(), map.get(pair).getHour() , map.get(pair).getUsage())) {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            mo.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                    }

                    Marker marker = mMap.addMarker(mo);
                    mMarkerArray.add(marker);

                }

                if (marker != null) {
                    if (view.equals("daily")) {
                        if (Tool.largerUsageDailyMap(getActivity(), map.get(pair).getUsage())) {
                            marker.setIcon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            marker.setIcon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                    }
                    else{
                        if (Tool.largerUsageHourlyMap(getActivity(), map.get(pair).getHour() , map.get(pair).getUsage())) {
                            marker.setIcon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            marker.setIcon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                    }

                    marker.setSnippet("Usage at " + availDate + " is : " + map.get(pair).getUsage() + "kWh");

                    if (marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }
                }
                super.onPostExecute(s);
            }
        };

        asyncTask.execute();
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
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Pair<Double,Double> pair = new Pair<>(marker.getPosition().latitude, marker.getPosition().longitude);
        int pos = sp_map_viewtype.getSelectedItemPosition();
        if(pos == 1)
            gerUsage(pair,"daily",marker, null);
        else
            gerUsage(pair, "hourly",marker, null);
        return false;
    }

    public Map<Pair<Double, Double>, MapResidUsage> getLocResMap() {
        return map;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMapView != null)
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
        dismissProgressDialog();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
