package com.apress.gerber.gradleweather;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.apress.gerber.weather.parse.WeatherParser;
import com.apress.gerber.weather.request.NationalWeatherRequest;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Clifton
 * Copyright 8/28/2014.
 */
public class NationalWeatherRequestData implements TemperatureData {

    public static final double DEFAULT_LATITUDE = 37.368830;
    public static final double DEFAULT_LONGITUDE = -122.036350;
    private final WeatherParser weatherParser;
    private final Context context;

    public NationalWeatherRequestData(Context context) {
        this.context = context;
        Location location = getLocation(context);
        weatherParser = new WeatherParser();
        String weatherXml = new NationalWeatherRequest(location).getWeatherXml();
        //National weather service returns XML data with embedded HTML <br> tags
        //These will choke the XML parser as they don't have closing syntax.
        String validXml = asValidXml(weatherXml);
        try {
            weatherParser.parse(new StringReader(validXml));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String asValidXml(String weatherXml) {
        return weatherXml.replaceAll("<br>","<br/>");
    }

    @Override
    public List<TemperatureItem> getTemperatureItems() {
        ArrayList<TemperatureItem> temperatureItems = new ArrayList<TemperatureItem>();
        List<Map<String, String>> forecast = weatherParser.getLastForecast();
        if (forecast!=null) {
            for(Map<String,String> eachEntry : forecast) {
                temperatureItems.add(new TemperatureItem(
                        context.getResources().getDrawable(R.drawable.progress_small),
                        eachEntry.get("iconLink"),
                        eachEntry.get("day"),
                        eachEntry.get("shortDescription"),
                        eachEntry.get("description")
                        ));
            }
        }
        return temperatureItems;
    }

    @Override
    public Map<String, String> getCurrentConditions() {
        return weatherParser.getCurrentConditions();
    }

    @Override
    public CharSequence getCity() {
        return weatherParser.getLocation();
    }

    private Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            return location;
        } else {
            Location defaultLocation = new Location(provider);
            defaultLocation.setLatitude(DEFAULT_LATITUDE);
            defaultLocation.setLongitude(DEFAULT_LONGITUDE);
            return defaultLocation;
        }
    }
}
