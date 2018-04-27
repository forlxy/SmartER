package com.smarter.com.smarter;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.smarter.tools.Tool.BASE_URL;

public class SecondRegisFragment extends Fragment {

    EditText et_date,et_fname,et_lname, et_email, et_phone, et_address,et_postcode;
    Spinner sp_num, sp_provider;
    Button bt_submit;

    String username, password;
    Resident newResident;
    Calendar myCalendar;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reg_second, container, false);

        context = getActivity();
        username = getArguments().getString("username");
        password = getArguments().getString("password");

        myCalendar = Calendar.getInstance();
        et_date = root.findViewById(R.id.et_re_date);
        et_fname = root.findViewById(R.id.et_re_firstname);
        et_lname = root.findViewById(R.id.et_re_lastname);
        et_email = root.findViewById(R.id.et_re_email);
        et_phone = root.findViewById(R.id.et_re_phone);
        et_address = root.findViewById(R.id.et_re_address);
        et_postcode = root.findViewById(R.id.et_re_postcode);
        sp_num = root.findViewById(R.id.spinner_re_num);
        sp_provider = root.findViewById(R.id.spinner_re_provider);
        bt_submit = root.findViewById(R.id.bt_re_submit);

        List<String> lineNum = Arrays.asList(getResources().getStringArray(R.array.num_array));
        List<String> lineProv = Arrays.asList(getResources().getStringArray(R.array.provider_array));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,lineNum);
        sp_num.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,lineProv);
        sp_provider.setAdapter(adapter);

        bt_submit.setOnClickListener(new View.OnClickListener() {
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


                            newResident = new Resident(resid, et_fname.getText().toString(), et_lname.getText().toString(), Datetools.regParse(et_date.getText().toString()),
                                    et_address.getText().toString(), et_postcode.getText().toString(),et_email.getText().toString(), et_phone.getText().toString(),
                                    Integer.parseInt(sp_num.getSelectedItem().toString()), sp_provider.getSelectedItem().toString());
                            String hash = LoginActivity.generateMD5(password);

                            Gson gson = new Gson();
                            String myData = gson.toJson(newResident);

                            JSONObject jsonResident = new JSONObject(myData);
                            jsonResident.put("dob", Datetools.toString(newResident.getDob()));
                            JSONObject jsonCredential = new JSONObject();

                            jsonCredential.put("username",username);
                            jsonCredential.put("passwdHash",hash);
                            jsonCredential.put("regdate", Datetools.getDate());
                            jsonCredential.put("resid", jsonResident);



                            redConnection = (HttpURLConnection) redRestful.openConnection();
                            redConnection.setReadTimeout(10000);
                            redConnection.setConnectTimeout(15000);
                            redConnection.setRequestMethod("POST");
                            redConnection.setRequestProperty("Content-Type", "application/json");
                            redConnection.setDoOutput(true);
                            String json = jsonResident.toString();
                            redConnection.getOutputStream().write(json.getBytes());

                            if (redConnection.getResponseCode() != 204) {
                                Log.e("Register","Connect Error: Code is " + redConnection.getResponseCode());
                                return "ERROR";
                            }

                            creConnection = (HttpURLConnection) creRestful.openConnection();
                            creConnection.setReadTimeout(10000);
                            creConnection.setConnectTimeout(15000);
                            creConnection.setRequestMethod("POST");
                            creConnection.setRequestProperty("Content-Type", "application/json");
                            creConnection.setDoOutput(true);
                            json = jsonCredential.toString();
                            creConnection.getOutputStream().write(json.getBytes());

                            if (creConnection.getResponseCode() != 204) {
                                Log.e("Register","Connect Error: Code is " + creConnection.getResponseCode());
                                return "ERROR";
                            }

                            redConnection.disconnect();
                            creConnection.disconnect();

                        } catch (ProtocolException e1) {
                            e1.printStackTrace();
                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (ParseException e1) {
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
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("resident", newResident); // using the (String name, Parcelable value) overload!
                            intent.putExtra("username", username); // using the (String name, Parcelable value) overload!
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        progDailog.dismiss();
                    }
                }.execute();


            }
        });


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        et_date.setText(sdf.format(myCalendar.getTime()));
    }

}
