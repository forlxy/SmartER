package com.smarter.com.smarter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smarter.tools.Datetools;
import com.smarter.tools.MapGeocode;
import com.smarter.tools.ParcelableUtil;
import com.smarter.tools.Tool;
import com.smarter.tools.Weather;

import org.json.JSONObject;

/**
 * Created by kasal on 31/03/2018.
 */

public class HomeFragment extends Fragment {

    TextView datetime;
    TextView firstname;
    TextView message;
    TextView temperature;
    Handler handler;

    ImageView imgView;
    Resident object;
    private Context context;

    private Activity activity;
    private ProgressDialog progDailog;

    public HomeFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        datetime = rootView.findViewById(R.id.tv_datetime);
        firstname = rootView.findViewById(R.id.tv_firstname);
        message = rootView.findViewById(R.id.tv_message);
        imgView = rootView.findViewById(R.id.imageView2);
        temperature = rootView.findViewById(R.id.tv_temperature);
        datetime.setText("Now is " + Datetools.getCurTime());
        firstname.setText("Welcome, " + object.getFname() + "!");


        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DbUpdateReceiver.class);
                byte[] bytes = ParcelableUtil.marshall(object);

                intent.putExtra("resident", bytes);
                intent.putExtra("flag", true);
                activity.sendBroadcast(intent);

            }
        });

        context = getActivity();
        if (Tool.largerUsageHourly(context,object.getResid())){
            imgView.setImageResource(R.drawable.self_improvement);
            message.setText("Your usage is larger than average! Need more improvement!");
        }
        else {
            imgView.setImageResource(R.drawable.good_job);
            message.setText("Your usage is smaller than average. Good job!");
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        if (savedInstanceState != null)
            object = savedInstanceState.getParcelable("resident");
        else
            object = ((MainActivity)activity).getObject();
//                getArguments().getParcelable("resident");
        getWeather();

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putParcelable("resident", object);
        // etc.
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            object = savedInstanceState.getParcelable("resident");
        }
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
    private void getWeather() {
        new AsyncTask<Void, Void, Pair<Double,Double>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
            }
            @Override
            protected Pair<Double, Double> doInBackground(Void... voids) {
                MapGeocode mg = new MapGeocode(object.getAddress());
                return mg.getLatLon();
            }
            @Override
            protected void onPostExecute(final Pair<Double, Double> pair) {
                WeatherUpdater wu = new WeatherUpdater();
                wu.execute(pair.first.toString(), pair.second.toString());
                dismissProgressDialog();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class WeatherUpdater extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            final JSONObject json = Weather.getJSON(context, params[0], params[1]);
            if(json == null){
                activity.runOnUiThread(new Runnable(){
                    public void run(){
                        Toast.makeText(context,context.getString(R.string.place_not_found),Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable(){
                    public void run(){
                        renderWeather(json);
                    }
                });
            }

            return params[0];
        }
        @Override
        protected void onPostExecute(String result) {
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void renderWeather(JSONObject json){
        try {
            JSONObject main = json.getJSONObject("main");
            temperature.setText("Current temperature is " + String.format("%.2f", main.getDouble("temp"))+ " ℃");
        }catch(Exception e){
            Log.e("home", "JSON ERROR");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}
