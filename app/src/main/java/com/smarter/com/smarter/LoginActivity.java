package com.smarter.com.smarter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.smarter.tools.Converter;

import com.smarter.tools.Datetools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.net.HttpURLConnection;

import static com.smarter.tools.Tool.BASE_URL;

/**
 * Created by kasal on 4/04/2018.
 */

public class LoginActivity extends Activity {

    EditText edUser, edPasswd;
    Button btLogin;
    TextView btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUser = findViewById(R.id.et_username);
        edPasswd = findViewById(R.id.et_password);
        btLogin = findViewById(R.id.bt_login);
        btRegister = findViewById(R.id.bt_regis);

        btLogin.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                    new AsyncTask<Void, Void, String>() {
                        ProgressDialog progDailog;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progDailog = new ProgressDialog(LoginActivity.this);
                            progDailog.setMessage("Loading...");
                            progDailog.setIndeterminate(false);
                            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progDailog.setCancelable(false);
                            progDailog.show();
                        }
                        @Override
                        protected String doInBackground(Void... params) {
                            String username = edUser.getText().toString();
                            String passwd = edPasswd.getText().toString();
                            passwd = generateMD5(passwd);
                            String url = BASE_URL + "/Assignment/webresources/restws.credential/findByUnPw/" + username + "/" + passwd;
                            try {
                                URL restful = new URL(url);
                                HttpURLConnection connection = null;
                                connection = (HttpURLConnection) restful.openConnection();
                                connection.setReadTimeout(10000);
                                connection.setConnectTimeout(15000);
                                connection.setRequestMethod("GET");
                                connection.setRequestProperty("Content-Type", "application/json");
                                connection.setRequestProperty("Accept", "application/json");

                                if (connection.getResponseCode() == 200) {

                                    InputStream responseBody = connection.getInputStream();
                                    InputStreamReader responseBodyReader =
                                            new InputStreamReader(responseBody, "UTF-8");
                                    JsonReader jsonReader = new JsonReader(responseBodyReader);

                                    List<Resident> rs = readResidentsArray(jsonReader);
                                    if (rs.size() != 1 || rs.get(0).getResid() == -1) { // Check
                                        
                                        return "Incorrect";
                                    } else {
                                        // Jump to main screen
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        Resident dataToSend = rs.get(0);
                                        intent.putExtra("resident", dataToSend); // using the (String name, Parcelable value) overload!
                                        intent.putExtra("username", username); // using the (String name, Parcelable value) overload!
//                                        intent.putExtra("address", rs.get(0).getAddress());
//                                        intent.putExtra("fname", rs.get(0).getFname());
//                                        intent.putExtra("postcode", rs.get(0).getPostcode());
//                                        intent.putExtra("providerName", rs.get(0).getProviderName());
//                                        intent.putExtra("resid", rs.get(0).getResid());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                    jsonReader.close();
                                } else {
                                    Log.e("Login","Connect Error: Code is " + connection.getResponseCode());
                                }
                                connection.disconnect();
                            } catch (ProtocolException e1) {
                                e1.printStackTrace();
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            return "Done";
                        }
                        @Override
                        protected void onPostExecute(String value) {

                            if (value.equals("Incorrect")){
                                Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                            } else if (value.equals("Done")){
//                                Toast.makeText(getApplicationContext(), "Login Done", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                            }
                            progDailog.dismiss();
                        }
                    }.execute();
            }

        });

        btRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Jump to register screen
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("username", edUser.getText()); // using the (String name, Parcelable value) overload!
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public List<Resident> readResidentsArray(JsonReader reader) throws IOException, ParseException {
        List<Resident> residents = new ArrayList<Resident>();

        reader.beginArray();
        while (reader.hasNext()) {
            residents.add(readResident(reader));
        }
        reader.endArray();
        return residents;
    }

    public Resident readResident(JsonReader reader) throws IOException, ParseException {
        Integer resid = -1;
        String fname = "";
        String sname = "";
        Date dob = new Date();
        String address = "";
        String postcode = "";
        String email = "";
        String mobile = "";
        int number = -1;
        String providerName = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("resid")) {
                resid = reader.nextInt();
            } else if (name.equals("fname")) {
                fname = reader.nextString();
            } else if (name.equals("sname")) {
                sname = reader.nextString();
            } else if (name.equals("dob")) {
                dob = Datetools.parse(reader.nextString());
            } else if (name.equals("address")) {
                address = reader.nextString();
            } else if (name.equals("postcode")) {
                postcode = reader.nextString();
            } else if (name.equals("email")) {
                email = reader.nextString();
            } else if (name.equals("mobile")) {
                mobile = reader.nextString();
            } else if (name.equals("number")) {
                number = reader.nextInt();
            } else if (name.equals("providerName")) {
                providerName = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Resident(resid, fname, sname, new java.sql.Date(dob.getTime()), address, postcode, email, mobile, number, providerName);
    }


    public static String generateMD5(String passwd) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        md.update(passwd.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method
        StringBuilder sb = new StringBuilder();
        for (byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
