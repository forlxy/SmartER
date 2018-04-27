package com.smarter.tools;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.smarter.com.smarter.Database;
import com.smarter.com.smarter.Resident;

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
import java.sql.Date;
import java.text.ParseException;

import static com.smarter.tools.Datetools.getDate;
import static com.smarter.tools.Datetools.getDateOffsetD;
import static com.smarter.tools.Datetools.parse;

public class Tool {
    public static String BASE_URL = "http://192.168.0.17:8080";
//    public static String BASE_URL = "http://118.139.93.83:8080";
    private static double HOURLY_THRESHOLD = 1.7;
    private static double DAILY_THRESHOLD = 21;

    public static boolean largerUsageDailyMap(Context context, double usage) {
        if (usage >= DAILY_THRESHOLD)
            return true;
        else
            return false;
    }

    public static boolean largerUsageHourlyMap(Context context, int hour, double usage) {
        if (Datetools.isWeekday() && hour >= 9 && hour <= 22) {
            if (usage >= HOURLY_THRESHOLD)
                return true;
        }
        return false;
    }


    public static boolean largerUsageHourly(Context context, Integer resid)
    {
        Database dbManager = new Database(context);
        try {
            dbManager.open();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        Cursor c = dbManager.getAllUsagesByresid(resid);
        if (c.moveToLast()) {
            int hour = c.getInt(3);
            double sum = 0.f;
            if (Datetools.isWeekday() && hour >= 9 && hour <= 22) {
                sum = c.getDouble(4) + c.getDouble(5) + c.getDouble(6);
                if (sum >= HOURLY_THRESHOLD)
                    return true;
            }
        }
        return false;
    }

    public static boolean largerUsageDaily(Context context, Integer resid)
    {
        Database dbManager = new Database(context);
        try {
            dbManager.open();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        Cursor c = dbManager.getAllUsagesByresid(resid);

        if (c.moveToFirst()) {
            double sum = 0.f;
            do {
                int hour = c.getInt(3);
                if (Datetools.isWeekday() && hour >= 9 && hour <= 22) {
                    sum += c.getDouble(4) + c.getDouble(5) + c.getDouble(6);
                }
            }while(c.moveToNext());
            if (sum >= DAILY_THRESHOLD)
                return true;
        }
        return false;
    }

//    public static boolean largerUsageHourlyRest(final Context context, Resident resident)  {
//
//        new AsyncTask<Void, Void, String>() {
//            ProgressDialog progDailog;
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progDailog = new ProgressDialog(context);
//                progDailog.setMessage("Loading...");
//                progDailog.setIndeterminate(false);
//                progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progDailog.setCancelable(false);
//                progDailog.show();
//            }
//            @Override
//            protected String doInBackground(Void... voids) {
//                if (type == 2) {
//
//                    int counter = 1;
//                    String view = "hourly";
//                    if (pos == 1) {
//                        view = "daily";
//                        counter = 30;
//                    }
//
//
//                    for (int j = counter; j >= dateOffset; j--) {
//                        String redURL = BASE_URL + "/Assignment/webresources/restws.usage/getHourDailyUsage/" +
//                                object.getResid()
////                            1
//                                + "/"
////                        + "2018-03-07"
//                                + Datetools.getDateOffset(j)
//                                + "/" + view;
//                        try {
//                            URL redRestful = new URL(redURL);
//                            HttpURLConnection redConnection;
//                            redConnection = (HttpURLConnection) redRestful.openConnection();
//                            redConnection.setReadTimeout(10000);
//                            redConnection.setConnectTimeout(15000);
//                            redConnection.setRequestMethod("GET");
//                            redConnection.setRequestProperty("Content-Type", "application/json");
//                            redConnection.setRequestProperty("Accept", "application/json");
//
//                            BufferedReader reader = new BufferedReader(
//                                    new InputStreamReader(redConnection.getInputStream()));
//
//                            StringBuilder jsonBuilder = new StringBuilder(1024);
//                            String tmp;
//                            while ((tmp = reader.readLine()) != null)
//                                jsonBuilder.append(tmp).append("\n");
//                            reader.close();
//                            JSONArray jsonRead = new JSONArray(jsonBuilder.toString());
//
//                            for (int i = 0; i < jsonRead.length(); i++) {
//                                JSONObject jsonObject = jsonRead.getJSONObject(i);
//                                usage = jsonObject.getDouble("usage");
//                                int hour = 0;
//                                if (pos == 0)
//                                    hour = jsonObject.getInt("hours");
//                                temp = jsonObject.getDouble("temperature");
//                                if (pos == 0) {
//                                    leftEntries.add(new Entry(hour, (float) usage));
//                                    rightEntries.add(new Entry(hour, (float) temp));
//                                } else {
//                                    leftEntries.add(new Entry(30 - j, (float) usage));
//                                    rightEntries.add(new Entry(30 - j, (float) temp));
//                                }
//                            }
//
//
//                            redConnection.disconnect();
//                            if (pos == 0 && jsonRead.length() == 0)
//                                j = j + 2;
//                            else if (pos == 1)
//                                continue;
//                            else
//                                break;
//                        } catch (ProtocolException e1) {
//                            e1.printStackTrace();
//                        } catch (MalformedURLException e1) {
//                            e1.printStackTrace();
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else if (type == 1){
//
//                    int counter = 1;
//                    String view = "hourly";
//                    if (pos == 1) {
//                        view = "daily";
//                        counter = 30;
//                    }
//
//                    for (int j = counter; j >= dateOffset; j--) {
//                        String redURL = BASE_URL + "/Assignment/webresources/restws.usage/getHourDailyUsage/" +
//                                object.getResid()
////                            1
//                                + "/"
////                        + "2018-03-07"
//                                + Datetools.getDateOffset(j)
//                                + "/" + view;
//                        try {
//                            URL redRestful = new URL(redURL);
//                            HttpURLConnection redConnection;
//                            redConnection = (HttpURLConnection) redRestful.openConnection();
//                            redConnection.setReadTimeout(10000);
//                            redConnection.setConnectTimeout(15000);
//                            redConnection.setRequestMethod("GET");
//                            redConnection.setRequestProperty("Content-Type", "application/json");
//                            redConnection.setRequestProperty("Accept", "application/json");
//
//                            BufferedReader reader = new BufferedReader(
//                                    new InputStreamReader(redConnection.getInputStream()));
//
//                            StringBuilder jsonBuilder = new StringBuilder(1024);
//                            String tmp;
//                            while ((tmp = reader.readLine()) != null)
//                                jsonBuilder.append(tmp).append("\n");
//                            reader.close();
//                            JSONArray jsonRead = new JSONArray(jsonBuilder.toString());
//
//                            for (int i = 0; i < jsonRead.length(); i++) {
//                                JSONObject jsonObject = jsonRead.getJSONObject(i);
//                                usage = jsonObject.getDouble("usage");
//                                int hour = 0;
//                                if (pos == 0)
//                                    hour = jsonObject.getInt("hours");
//                                if (pos == 0) {
//                                    barEntries.add(new BarEntry(hour, (float) usage));
//                                } else {
//                                    barEntries.add(new BarEntry(30 - j, (float) usage));
//                                }
//                            }
//
//
//                            redConnection.disconnect();
//                            if (pos == 0 && jsonRead.length() == 0)
//                                j = j + 2;
//                            else if (pos == 1)
//                                continue;
//                            else
//                                break;
//                        } catch (ProtocolException e1) {
//                            e1.printStackTrace();
//                        } catch (MalformedURLException e1) {
//                            e1.printStackTrace();
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else if (type == 0) {
//
//
//                    String URL = null;
//                    try {
//                        URL = BASE_URL + "/Assignment/webresources/restws.usage/getDailyUsageAppliance/" +
//                                object.getResid() + "/" + Datetools.toString(Datetools.regParse(datePicker.getText().toString()));
//                        URL redRestful = new URL(URL);
//                        HttpURLConnection connection;
//                        connection = (HttpURLConnection) redRestful.openConnection();
//                        connection.setReadTimeout(10000);
//                        connection.setConnectTimeout(15000);
//                        connection.setRequestMethod("GET");
//                        connection.setRequestProperty("Content-Type", "application/json");
//                        connection.setRequestProperty("Accept", "application/json");
//
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(connection.getInputStream()));
//
//                        StringBuffer jsonBuilder = new StringBuffer(1024);
//                        String tmp;
//                        while ((tmp = reader.readLine()) != null)
//                            jsonBuilder.append(tmp).append("\n");
//                        reader.close();
//                        JSONArray jsonRead = new JSONArray(jsonBuilder.toString());
//
//
//                        for (int i = 0; i < jsonRead.length(); i++) {
//                            JSONObject jsonObject = jsonRead.getJSONObject(i);
//                            double fridge, aircon, washmach;
//                            fridge = jsonObject.getDouble("fridge");
//                            aircon = jsonObject.getDouble("aircon");
//                            washmach = jsonObject.getDouble("washingmachine");
//
//                            if (fridge != 0)
//                                pieEntries.add(new PieEntry((float) fridge, "Fridge"));
//                            if (aircon != 0)
//                                pieEntries.add(new PieEntry((float) aircon, "Air Conditioner"));
//                            if (washmach != 0)
//                                pieEntries.add(new PieEntry((float) washmach, "Wash Machine"));
//                        }
//
//
//                        connection.disconnect();
//
//                    } catch (ProtocolException e1) {
//                        e1.printStackTrace();
//                    } catch (MalformedURLException e1) {
//                        e1.printStackTrace();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                return "Done";
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                progDailog.dismiss();
//                if (type == 2)
//                    showLineChart(pos);
//                else if (type == 1)
//                    showBarChart(pos);
//                else if (type == 0)
//                    showPieChart(pos);
//
//            }
//        }.execute();
//
//    }



    public static String getUsageHourly(Context context, Integer resid)
    {
        Database dbManager = new Database(context);
        double sum = 0;
        try {
            dbManager.open();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        Cursor c = dbManager.getAllUsagesByresid(resid);
        if (c.moveToLast()) {
            sum = c.getDouble(4) + c.getDouble(5) + c.getDouble(6);
        }
        return String.valueOf(sum);
    }

    public static String getUsageDaily(Context context, Integer resid)
    {
        Database dbManager = new Database(context);
        double sum = 0;
        try {
            dbManager.open();
        }catch(SQLException e) {
            e.printStackTrace();
        }
        Cursor c = dbManager.getAllUsagesByresid(resid);
        if (c.moveToFirst()) {
            do {
                sum += c.getDouble(4) + c.getDouble(5) + c.getDouble(6);
            }while(c.moveToNext());
        }
        return String.valueOf(sum);
    }
}
