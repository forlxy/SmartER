package com.smarter.tools;


//import android.icu.util.TimeZone;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kasal on 7/04/2018.
 */

public class Datetools {
    public static Date parse(String input) throws java.text.ParseException {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );

        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );
            input = s0  + "GMT"+ s1;
        }

        return df.parse( input );

    }

    public static Date regParse(String input) throws java.text.ParseException {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "dd/MM/yyyy" );
        return df.parse( input );

    }

    public static String getDateOffset(int offset)
    {
        Date c = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(c);
        calendar.add(Calendar.DATE, 0 - offset);
        Date offsetDay = calendar.getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(offsetDay);
    }


    public static Date getDateOffsetD(int offset)
    {
        Date c = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(c);
        calendar.add(Calendar.DATE, 0 - offset);
        Date offsetDay = calendar.getTime();

        return offsetDay;
    }


    public static String getCurTime()
    {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
        return df.format(c);
    }


    public static String getQueryDate(Date c)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
        return df.format(c);
    }

    public static int getCurHour()
    {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("HH");
        String formattedDate = df.format(c);
        return Integer.parseInt(formattedDate);
    }

    public static String getDate(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        String formattedDate;
        formattedDate = df.format(c);
        if ( formattedDate.endsWith( "Z" ) ) {
            formattedDate = formattedDate.substring( 0, formattedDate.length() - 1) + "-00:00";
        } else {
            int inset = 6;
            String s0 = formattedDate.substring( 0, formattedDate.length() - inset - 3 );
            String s1 = formattedDate.substring( formattedDate.length() - inset, formattedDate.length() );
            formattedDate = s0 + s1;
        }
        return formattedDate;
    }

    public static boolean isWeekday(){
        Calendar calendar = Calendar.getInstance();
        if( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            return false;
        return true;
    }


    public static String toString( Date date ) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        String formattedDate = df.format( date );
        if ( formattedDate.endsWith( "Z" ) ) {
            formattedDate = formattedDate.substring( 0, formattedDate.length() - 1) + "-00:00";
        } else {
            int inset = 6;
            String s0 = formattedDate.substring( 0, formattedDate.length() - inset - 3 );
            String s1 = formattedDate.substring( formattedDate.length() - inset, formattedDate.length() );
            formattedDate = s0 + s1;
        }
        return formattedDate;

    }
}
