package com.smarter.com.smarter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smarter.tools.Datetools;

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

import static com.smarter.tools.Tool.BASE_URL;

public class FirstRegisFragment extends Fragment {
    EditText et_username,et_password,et_re_password;
    Button btNext;
    
    OnNext mCallback;

    // Container Activity must implement this interface
    public interface OnNext {
        public void onNext(String username, String password);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNext) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onNext");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reg_first, container, false);

        et_username = root.findViewById(R.id.et_re_username);
        et_password = root.findViewById(R.id.et_re_password);
        et_re_password = root.findViewById(R.id.et_re_re_password);
        btNext = root.findViewById(R.id.bt_next);


        btNext.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, String>() {
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
                        String creURL = BASE_URL + "/Assignment/webresources/restws.credential/";
                        try {

                            if (!et_password.getText().toString().equals(et_re_password.getText().toString())) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity(), "Passwords are not same!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                return "Error: Password";
                            }

                            URL redRestful = new URL(redURL);
                            URL creRestful = new URL(creURL);
                            HttpURLConnection redConnection, creConnection;
                            redConnection = (HttpURLConnection) redRestful.openConnection();
                            redConnection.setReadTimeout(10000);
                            redConnection.setConnectTimeout(15000);
                            redConnection.setRequestMethod("GET");
                            redConnection.setRequestProperty("Content-Type", "application/json");
                            redConnection.setRequestProperty("Accept", "application/json");

                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(redConnection.getInputStream()));

                            StringBuffer jsonBuilder = new StringBuffer(1024);
                            String tmp;
                            while ((tmp = reader.readLine()) != null)
                                jsonBuilder.append(tmp).append("\n");
                            reader.close();
                            JSONArray jsonRead = new JSONArray(jsonBuilder.toString());

                            Integer resid = jsonRead.getJSONObject(jsonRead.length() - 1).getInt("resid") + 1;

                            creConnection = (HttpURLConnection) creRestful.openConnection();
                            creConnection.setReadTimeout(10000);
                            creConnection.setConnectTimeout(15000);
                            creConnection.setRequestMethod("GET");
                            creConnection.setRequestProperty("Content-Type", "application/json");
                            creConnection.setRequestProperty("Accept", "application/json");

                            reader = new BufferedReader(
                                    new InputStreamReader(creConnection.getInputStream()));

                            jsonBuilder = new StringBuffer(1024);
                            while ((tmp = reader.readLine()) != null)
                                jsonBuilder.append(tmp).append("\n");
                            reader.close();
                            jsonRead = new JSONArray(jsonBuilder.toString());


                            for (int i = 0; i < jsonRead.length(); i++)
                            {
                                String username = jsonRead.getJSONObject(i).getString("username");
                                if (et_username.getText().toString().equals(username))
                                {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(), "Username already existed!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    return "Error: Username";
                                }
                            }


                            redConnection.disconnect();
                            creConnection.disconnect();

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
                        super.onPostExecute(s);
                        if(s.equals("Done")) {
                            mCallback.onNext(et_username.getText().toString(),et_password.getText().toString());
                        }
                        progDailog.dismiss();
                    }
                }.execute();

            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
