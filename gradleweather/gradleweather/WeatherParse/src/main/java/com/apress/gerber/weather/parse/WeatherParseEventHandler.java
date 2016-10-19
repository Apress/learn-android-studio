package com.apress.gerber.weather.parse;

import java.util.Map;

/**
 * Created by Clifton
 * Copyright 8/31/2014.
 */
public class WeatherParseEventHandler {
    private final WeatherEventCallback weatherEventCallback;

    String text = null;
    String tempType = null;
    String timeLayout = null;
    int forecastCount = 0;

    public WeatherParseEventHandler(WeatherEventCallback weatherEventCallback) {
        this.weatherEventCallback = weatherEventCallback;
    }

    interface WeatherEventCallback {
        void onCurrentCondition(String type, String condition);

        void onForecastDetail(String timeLayout, int idx, String detailType, String value);

        void onLocation(String location);
    }

    public void onText(String newText) {
        text = newText;
    }

    public void onEndElement(String closeTag, String currentTag) {
        if(closeTag.equals("value") && tempType!=null && currentTag.equals("temperature")) {
            weatherEventCallback.onCurrentCondition(tempType, text);
            tempType = null;
        } else if(closeTag.equals("layout-key")) {
            timeLayout = text;
        } else if(closeTag.equals("time-layout") || closeTag.equals("conditions-icon") || closeTag.equals("weather")) {
            forecastCount = 0;
        } else if(closeTag.equals("icon-link") ) {
            weatherEventCallback.onForecastDetail(timeLayout, forecastCount++, "iconLink", text);
        } else if(closeTag.equals("text") && currentTag.equals("wordedForecast") ) {
            weatherEventCallback.onForecastDetail(timeLayout, forecastCount++, "description", text);
        } else if(closeTag.equals("description") && currentTag.equals("location")) {
            weatherEventCallback.onLocation(text);
        }
    }

    public void onStartElement(String tag, Map<String, String> attributes) {
        if(tag.equals("temperature")) {
            tempType = attributes.get("type");
        } else if(tag.equals("start-valid-time")) {
            weatherEventCallback.onForecastDetail(timeLayout, forecastCount++, "day", attributes.get("period-name"));
        } else if(tag.equals("weather-conditions") ) {
            weatherEventCallback.onForecastDetail(timeLayout, forecastCount++, "shortDescription", attributes.get("weather-summary"));
        } else if(tag.equals("conditions-icon") || tag.equals("weather")) {
            timeLayout = attributes.get("time-layout");
        }
    }

}
