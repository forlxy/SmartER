package com.smarter.com.smarter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.SupportMapFragment;
import com.smarter.tools.Datetools;
import com.smarter.tools.MyMarkerView;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static com.smarter.tools.Datetools.getDateOffsetD;
import static com.smarter.tools.Tool.BASE_URL;

public class ReportFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    String[] label;
    boolean changeView, changeType;
//    Button btChart;
    Spinner spChart;
    Spinner spChartType;
    LineChart lineChart;
    BarChart barChart;
    PieChart pieChart;
    Resident resident;
    double usage;
    double temp;
    List<Entry> leftEntries;
    List<Entry> rightEntries;
    List<BarEntry> barEntries;
    List<PieEntry> pieEntries;;
    AsyncTask<Void, Void, ArrayList<String>> asyncTask;
    private Resident object;
    LinearLayout chartLayout;
    Calendar myCalendar;
    List<String> list;
    ArrayAdapter<String> adapter;
    EditText datePicker;

    String availableDate;
    int viewType;

    int type;
    private ProgressDialog progDailog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View newView = inflater.inflate(R.layout.fragment_report, container, false);
        availableDate = "";
        spChartType = newView.findViewById(R.id.sp_charttype);
        list = Arrays.asList(getResources().getStringArray(R.array.charttype_array));
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,list);
        spChartType.setAdapter(adapter);

        spChartType.setOnItemSelectedListener(this);

        datePicker = new EditText(getActivity());
        datePicker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        datePicker.setHint("Select query date");

        lineChart = new LineChart(getActivity());
        barChart = new BarChart(getActivity());
        pieChart = new PieChart(getActivity());

        lineChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        barChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        pieChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//        Resources res = getResources();
        spChart = new Spinner(getActivity());
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.chart_array, R.layout.spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        list = Arrays.asList(getResources().getStringArray(R.array.chart_array));
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,list);

        spChart.setAdapter(adapter);
        spChart.setOnItemSelectedListener(this);

        chartLayout = newView.findViewById(R.id.chart_layout);

        chartLayout.removeAllViews();

        viewType = 0;
        type = 0;

//        chartLayout.addView(lineChart);

        barEntries = new ArrayList<>();
        pieEntries = new ArrayList<>();
        leftEntries = new ArrayList<>();
        rightEntries = new ArrayList<>();
        return newView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeType = false;
        changeView = true;
        myCalendar = Calendar.getInstance();

        if (savedInstanceState != null)
            object = savedInstanceState.getParcelable("resident");
        else
            object = ((MainActivity)getActivity()).getObject();
//        object = getArguments().getParcelable("resident");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putParcelable("resident", object);
    }

    private void clearAllEntries()
    {
        leftEntries.clear();
        rightEntries.clear();
        barEntries.clear();
        pieEntries.clear();
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
    
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_charttype)
        {
            lineChart = new LineChart(getActivity());
            barChart = new BarChart(getActivity());
            pieChart = new PieChart(getActivity());

            lineChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            barChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            pieChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            spChart = new Spinner(getActivity());
            list = Arrays.asList(getResources().getStringArray(R.array.chart_array));
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,list);

            spChart.setAdapter(adapter);
            spChart.setOnItemSelectedListener(this);
            chartLayout.removeAllViews();

            type = position;
            if (type == 0){
                datePicker = new EditText(getActivity());
                datePicker.setHint("Select query date");
                chartLayout.addView(datePicker);
                chartLayout.addView(pieChart);
                updateChart(viewType, type);
            } else if (type == 1){
                chartLayout.addView(spChart);
                chartLayout.addView(barChart);
            } else if (type == 2) {
                chartLayout.addView(spChart);
                chartLayout.addView(lineChart);
            }
        } else {
            viewType = position;
            updateChart(viewType, type);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateChart(int pos, int type)
    {
        clearAllEntries();
        if (pos == 0) {
            getUsageTemp(1, pos, type);
        } else {
            getUsageTemp( 1 ,pos, type);
        }

    }

    @SuppressLint("StaticFieldLeak")
    public void getUsageTemp(final int dateOffset, final int pos, final int type)
    {

        asyncTask = new AsyncTask<Void, Void, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
            }
            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                ArrayList returnList = new ArrayList();
                returnList.clear();
                if (type == 2) {
                    int counter = 1;
                    String view = "hourly";
                    if (pos == 1) {
                        view = "daily";
//                        counter = 1;
                    }

                    for (int j = counter; j >= dateOffset && j - dateOffset <= 100; j--) {
                        String redURL = BASE_URL + "/Assignment/webresources/restws.usage/getHourDailyUsageForReport/" +
                                object.getResid()
                                + "/"
                                + Datetools.getDateOffset(j)
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

                            for (int i = 0; i < jsonRead.length(); i++) {
                                JSONObject jsonObject = jsonRead.getJSONObject(i);
                                usage = jsonObject.getDouble("usage");
                                int hour = 0;
                                if (pos == 0)
                                    hour = jsonObject.getInt("hours");
                                temp = jsonObject.getDouble("temperature");
                                if (pos == 0) {
                                    availableDate = Datetools.getDateOffset(j);
                                    leftEntries.add(new Entry(hour, (float) usage));
                                    rightEntries.add(new Entry(hour, (float) temp));
                                } else {
                                    availableDate = "";
                                    leftEntries.add(new Entry(i, (float) usage));
                                    rightEntries.add(new Entry(i, (float) temp));


                                    DateFormat dateFormat = new SimpleDateFormat(
                                            "EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);

                                    String dateString = jsonObject.getString("usagedate");
                                    int inset = 18;
                                    String s0 = dateString.substring(0, inset);
                                    String s1 = dateString.substring(dateString.length() - 5, dateString.length());
                                    dateString = s0 + s1;


                                    Date date = dateFormat.parse(dateString);
                                    returnList.add(Datetools.getQueryDate(date));
                                }
                            }


                            redConnection.disconnect();
                            if (pos == 0 && jsonRead.length() == 0)
                                j = j + 2;
                            else if (pos == 1)
                                continue;
                            else
                                break;
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
                    }
                } else if (type == 1){

                    int counter = 1;
                    String view = "hourly";
                    if (pos == 1) {
                        view = "daily";
//                        counter = 30;
                    }

                    for (int j = counter; j >= dateOffset && j - dateOffset <= 100; j--) {
                        String redURL = BASE_URL + "/Assignment/webresources/restws.usage/getHourDailyUsageForReport/" +
                                object.getResid()
//                            1
                                + "/"
//                        + "2018-04-01"
                                + Datetools.getDateOffset(j)
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

                            for (int i = 0; i < jsonRead.length(); i++) {
                                JSONObject jsonObject = jsonRead.getJSONObject(i);
                                usage = jsonObject.getDouble("usage");
                                int hour = 0;
                                if (pos == 0)
                                    hour = jsonObject.getInt("hours");
                                if (pos == 0) {
                                    availableDate = Datetools.getDateOffset(j);
                                    barEntries.add(new BarEntry(hour, (float) usage));
                                } else {
                                    availableDate = "";
                                    barEntries.add(new BarEntry(i, (float) usage));

                                    DateFormat dateFormat = new SimpleDateFormat(
                                            "EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);

                                    String dateString = jsonObject.getString("usagedate");
                                    int inset = 18;
                                    String s0 = dateString.substring(0, inset);
                                    String s1 = dateString.substring(dateString.length() - 5, dateString.length());
                                    dateString = s0 + s1;


                                    Date date = dateFormat.parse(dateString);
                                    returnList.add(Datetools.getQueryDate(date));
                                }
                            }
                            redConnection.disconnect();


                            if (pos == 0 && jsonRead.length() == 0)
                                j = j + 2;
                            else if (pos == 1)
                                continue;
                            else
                                break;
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
                    }
                } else if (type == 0) {


                    String URL = null;
                    try {
                        URL = BASE_URL + "/Assignment/webresources/restws.usage/getDailyUsageAppliance/" +
                                object.getResid() + "/" + Datetools.toString(Datetools.regParse(datePicker.getText().toString()));
                        URL redRestful = new URL(URL);
                        HttpURLConnection connection;
                        connection = (HttpURLConnection) redRestful.openConnection();
                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));

                        StringBuffer jsonBuilder = new StringBuffer(1024);
                        String tmp;
                        while ((tmp = reader.readLine()) != null)
                            jsonBuilder.append(tmp).append("\n");
                        reader.close();
                        JSONArray jsonRead = new JSONArray(jsonBuilder.toString());


                        for (int i = 0; i < jsonRead.length(); i++) {
                            JSONObject jsonObject = jsonRead.getJSONObject(i);
                            double fridge, aircon, washmach;
                            fridge = jsonObject.getDouble("fridge");
                            aircon = jsonObject.getDouble("aircon");
                            washmach = jsonObject.getDouble("washingmachine");

                            if (fridge != 0)
                                pieEntries.add(new PieEntry((float) fridge, "Fridge"));
                            if (aircon != 0)
                                pieEntries.add(new PieEntry((float) aircon, "Air Conditioner"));
                            if (washmach != 0)
                                pieEntries.add(new PieEntry((float) washmach, "Wash Machine"));
                        }


                        connection.disconnect();

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
                }

                return returnList;
            }

            @Override
            protected void onPostExecute(ArrayList<String> s) {
                super.onPostExecute(s);
                dismissProgressDialog();
                if (type == 2)
                    showLineChart(pos, s);
                else if (type == 1)
                    showBarChart(pos, s);
                else if (type == 0)
                    showPieChart(pos);

            }
        }.execute();

//        String str_result= asyncTask.get();

    }

    private void showBarChart(int pos, ArrayList<String> s) {

        barChart.getDescription().setText(availableDate);
        if (barEntries.size() != 0) {
            BarDataSet set = new BarDataSet(barEntries, "Usage");
            BarData data = new BarData(set);
            data.setBarWidth(0.9f); // set custom bar width

            //implementing IAxisValueFormatter interface to show year values not as float/decimal
            if (pos == 0) {
                label = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
            } else {
                if(s.size() != 0)
                    label = s.toArray(new String[s.size()]);
                else
                    label =
                        new String[]{Datetools.getDateOffset(30), Datetools.getDateOffset(29), Datetools.getDateOffset(28), Datetools.getDateOffset(27), Datetools.getDateOffset(26),
                        Datetools.getDateOffset(25), Datetools.getDateOffset(24), Datetools.getDateOffset(23), Datetools.getDateOffset(22),
                        Datetools.getDateOffset(21), Datetools.getDateOffset(20), Datetools.getDateOffset(19), Datetools.getDateOffset(18),
                        Datetools.getDateOffset(17), Datetools.getDateOffset(16), Datetools.getDateOffset(15), Datetools.getDateOffset(14),
                        Datetools.getDateOffset(13), Datetools.getDateOffset(12), Datetools.getDateOffset(11), Datetools.getDateOffset(10),
                        Datetools.getDateOffset(9), Datetools.getDateOffset(8), Datetools.getDateOffset(7), Datetools.getDateOffset(6),
                        Datetools.getDateOffset(5), Datetools.getDateOffset(4), Datetools.getDateOffset(3), Datetools.getDateOffset(2), Datetools.getDateOffset(1)};
            }
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value >= label.length || value < 0)
                        return "EMPTY";
                    return label[(int) value];
                }
            };

            XAxis xAxis = barChart.getXAxis();
            xAxis.setDrawAxisLine(true);
            xAxis.setValueFormatter(formatter);
            // minimum axis-step (interval) is 1,if no, the same value will be displayed multiple times
            xAxis.setGranularity(barEntries.size() / 12 + 1);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            barChart.setData(data);
            barChart.setFitBars(true); // make the x-axis fit exactly all bars
            barChart.invalidate(); // refresh
        } else {
            barChart.clear();
        }
    }

    @SuppressLint({"StaticFieldLeak", "ClickableViewAccessibility"})
    private void showPieChart(int pos) {


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                updateChart(viewType, type);
            }

        };

        datePicker.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));

                    Date today = getDateOffsetD(0);
                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    c.add( Calendar.YEAR, -20 );
                    long minDate = c.getTime().getTime();

                    c = Calendar.getInstance();
                    c.setTime(today);
                    long maxDate = c.getTime().getTime();

                    pickerDialog.getDatePicker().setMaxDate(maxDate);
                    pickerDialog.getDatePicker().setMinDate(minDate);
                    pickerDialog.show();
                }
                return true; // the listener has consumed the event
            }
        });

        PieDataSet set = new PieDataSet(pieEntries, "");
        ArrayList<Integer> colors = new ArrayList<Integer>();


        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        set.setColors(colors);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setCenterText("Usage");
        pieChart.setUsePercentValues(true);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

//        MyMarkerView marker = new MyMarkerView(getActivity(),R.layout.marker_view);
//        pieChart.setMarker(marker);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        datePicker.setText(sdf.format(myCalendar.getTime()));
    }


    public void showLineChart(int pos, ArrayList<String> s) {
        lineChart.getDescription().setText(availableDate);
        if (leftEntries.size() != 0 && rightEntries.size() != 0) {
            if (lineChart.getData() != null &&
                    lineChart.getData().getDataSetCount() > 0) {
                LineDataSet set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                LineDataSet set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
                set1.setValues(leftEntries);
                set2.setValues(rightEntries);
                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
            } else {

                LineDataSet dataSet = new LineDataSet(leftEntries, "Usage");

                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(ColorTemplate.getHoloBlue());
                dataSet.setCircleColor(Color.BLACK);
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(3f);
                dataSet.setFillAlpha(65);
                dataSet.setFillColor(ColorTemplate.getHoloBlue());
                dataSet.setHighLightColor(Color.rgb(244, 117, 117));
                dataSet.setDrawCircleHole(false);


                // create a dataset and give it a type
                LineDataSet dataSet2 = new LineDataSet(rightEntries, "Temperature");
                dataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
                dataSet2.setColor(Color.RED);
                dataSet2.setCircleColor(Color.BLACK);
                dataSet2.setLineWidth(2f);
                dataSet2.setCircleRadius(3f);
                dataSet2.setFillAlpha(65);
                dataSet2.setFillColor(Color.RED);
                dataSet2.setDrawCircleHole(false);
                dataSet2.setHighLightColor(Color.rgb(244, 117, 117));
                //set2.setFillFormatter(new MyFillFormatter(900f));

                dataSet.setDrawValues(true);
                dataSet2.setDrawValues(true);

                // create a data object with the datasets
                LineData data = new LineData(dataSet, dataSet2);
//                    data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);

                // set data
                if (leftEntries.size() != 0 || rightEntries.size() != 0)
                    lineChart.setData(data);
            }


            //implementing IAxisValueFormatter interface to show year values not as float/decimal
            if (pos == 0) {
                label = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
            } else {
                if(s.size() != 0)
                    label = s.toArray(new String[s.size()]);
                else
                    label =
                        new String[]{Datetools.getDateOffset(30), Datetools.getDateOffset(29), Datetools.getDateOffset(28), Datetools.getDateOffset(27), Datetools.getDateOffset(26),
                        Datetools.getDateOffset(25), Datetools.getDateOffset(24), Datetools.getDateOffset(23), Datetools.getDateOffset(22),
                        Datetools.getDateOffset(21), Datetools.getDateOffset(20), Datetools.getDateOffset(19), Datetools.getDateOffset(18),
                        Datetools.getDateOffset(17), Datetools.getDateOffset(16), Datetools.getDateOffset(15), Datetools.getDateOffset(14),
                        Datetools.getDateOffset(13), Datetools.getDateOffset(12), Datetools.getDateOffset(11), Datetools.getDateOffset(10),
                        Datetools.getDateOffset(9), Datetools.getDateOffset(8), Datetools.getDateOffset(7), Datetools.getDateOffset(6),
                        Datetools.getDateOffset(5), Datetools.getDateOffset(4), Datetools.getDateOffset(3), Datetools.getDateOffset(2), Datetools.getDateOffset(1)};
            }
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if (value >= label.length || value < 0)
                        return "EMPTY";
                    return label[(int) value];
                }
            };

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setDrawAxisLine(true);
            xAxis.setValueFormatter(formatter);
            // minimum axis-step (interval) is 1,if no, the same value will be displayed multiple times
            xAxis.setGranularity(leftEntries.size() / 6 + 1);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
//            leftAxis.setAxisMaximum(10);
//            leftAxis.setAxisMinimum(0);
//                leftAxis.setDrawGridLines(true);
//                leftAxis.setGranularityEnabled(true);

            YAxis rightAxis = lineChart.getAxisRight();
            rightAxis.setTextColor(Color.RED);
            rightAxis.setAxisMaximum(45);
            rightAxis.setAxisMinimum(-5);
//                rightAxis.setDrawGridLines(false);
//                rightAxis.setDrawZeroLine(false);
//                rightAxis.setGranularityEnabled(false);


//            lineChart.animateX(2500);
            lineChart.invalidate();
        } else {
            lineChart.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}
