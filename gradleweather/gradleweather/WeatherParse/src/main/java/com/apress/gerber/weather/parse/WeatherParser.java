package com.apress.gerber.weather.parse;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherParser implements WeatherParseEventHandler.WeatherEventCallback {
    XmlPullParser xpp;
    List<String> currentTag = new ArrayList<String>();
    Map<String, String> currentAttributes;
    Map<String, String> currentConditions = new HashMap<String, String>();
    String location = "?";
    final static Pattern pattern = Pattern.compile("k-p\\d+h-n(\\d+)-\\d+");

    public WeatherParser() {
        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Pull parser not available");
        }
        factory.setNamespaceAware(true);
        try {
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            throw new RuntimeException("Pull parser can not be created.");
        }
    }

    public void parse(Reader xml) throws XmlPullParserException, IOException {
        WeatherParseEventHandler eventHandler = new WeatherParseEventHandler(this);
        xpp.setInput(xml);
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                String tag = xpp.getName();
                currentAttributes = attributes();
                currentTag.add(tag);
                eventHandler.onStartElement(tag, currentAttributes);
            } else if(eventType == XmlPullParser.END_TAG) {
                String closeTag = currentTag.remove(currentTag.size() - 1);
                String priorTag = currentTag.size() > 0 ? currentTag.get(currentTag.size() - 1) : "";
                eventHandler.onEndElement(closeTag, priorTag);
            } else if(eventType == XmlPullParser.TEXT) {
                eventHandler.onText(xpp.getText());
            }
            eventType = xpp.next();
        }
    }

    Map<String, List> forecastByTimeLayout = new HashMap<String, List>();

    public List<Map<String, String>> findForecast(String timeLayout) {
        if(!forecastByTimeLayout.containsKey(timeLayout)) {
            forecastByTimeLayout.put(timeLayout, new ArrayList<Map<String, String>>());
        }
        return (List<Map<String, String>>)forecastByTimeLayout.get(timeLayout);
    }

    public String getLocation() {
        return location;
    }

    public String getCurrent(String key) {
        return currentConditions.get(key);
    }

    public List<Map<String, String>> getForecast(String timeLayout) {
        return forecastByTimeLayout.get(timeLayout);
    }

    public Collection<String> getAvailableForecasts() {
        return forecastByTimeLayout.keySet();
    }

    public String lastForecast() {
        int max = 0;
        String last = null;
        for (String eachKey : getAvailableForecasts()) {
            Matcher matcher = pattern.matcher(eachKey);
            if (matcher.matches()) {
                int num = Integer.parseInt(matcher.group(1));
                max = Math.max(num, max);
                if(num==max) last = eachKey;
            }
        }
        return last;
    }

    public List<Map<String, String>> getLastForecast() {
        return getForecast(lastForecast());
    }

    public Map<String, String> getCurrentConditions() {
        return currentConditions;
    }

    @Override
    public void onCurrentCondition(String type, String condition) {
        currentConditions.put(type, condition);
    }

    @Override
    public void onForecastDetail(String timeLayout, int idx, String detailType, String value) {
        addForecastValue(findForecast(timeLayout), idx, detailType, value);
    }

    @Override
    public void onLocation(String location) {
        this.location = location;
    }

    private Map<String, String> attributes() {
        Map<String, String> all = new HashMap<String, String>();
        for(int i=0; i < xpp.getAttributeCount(); i++) {
            all.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }
        return all;
    }

    private void addForecastValue(List<Map<String, String>> forecast, int index, String key, String value) {
        while(forecast.size()-1 < index)
            forecast.add(new HashMap<String, String>());
        forecast.get(index).put(key, value);
    }
}

