package com.apress.gerber.weather.request;

import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Clifton
 * Copyright 8/28/2014.
 */
public class NationalWeatherRequest {

    public static final String NATIONAL_WEATHER_SERVICE =
            "http://forecast.weather.gov/MapClick.php?lat=%f&lon=%f&FcstType=dwml";
    private final URL url;

    public NationalWeatherRequest(Location location) {
        url = createUrl(location);
    }

    public String getWeatherXml() {
        InputStream inputStream = getInputStream(url);
        return readWeatherXml(inputStream);
    }

    private String readWeatherXml(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();
        if (inputStream!=null) {
            BufferedReader weatherReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                for(String eachLine = weatherReader.readLine(); eachLine!=null; eachLine = weatherReader.readLine()) {
                    builder.append(eachLine);
                }
            } catch (IOException e) {
                log("Exception reading data from Nat'l weather site " + e);
                e.printStackTrace();
            }
        }
        String weatherXml = builder.toString();
        log("Weather data " + weatherXml);
        return weatherXml;
    }

    private InputStream getInputStream(URL url) {
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            log("Exception opening Nat'l weather URL " + e);
            e.printStackTrace();
        }
        return inputStream;
    }

    private URL createUrl(Location location) {
        URL url;
        try {
            url = new URL(String.format(NATIONAL_WEATHER_SERVICE, location.getLatitude(), location.getLongitude()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL for National Weather Service: " +
            NATIONAL_WEATHER_SERVICE);
        }
        return url;
    }

    private int log(String eachLine) {
        return Log.d(getClass().getName(), eachLine);
    }
}
