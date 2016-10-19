package com.apress.gerber.weather.parse;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by Clifton
 * Copyright 8/28/2014.
 */
public class WeatherParseTest extends TestCase {

    private WeatherParser weather;

    private String asString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        for(String eachLine = reader.readLine(); eachLine != null; eachLine = reader.readLine()) {
            builder.append(eachLine);
        }
        return builder.toString();
    }

    public void setUp() throws IOException, XmlPullParserException {
        URL weatherXml = getClass().getResource("/weather.xml");
        assertNotNull("Test requires weather.xml as a resource at the CP root.", weatherXml);
        String givenXml = asString(weatherXml.openStream());
        this.weather = new WeatherParser();
        weather.parse(new StringReader(givenXml.replaceAll("<br>", "<br/>")));
    }

    public void testCanSeeCurrentTemp() {
        assertEquals(weather.getCurrent("apparent"), "63");
        assertEquals(weather.getCurrent("minimum"), "59");
        assertEquals(weather.getCurrent("maximum"), "81");
        assertEquals(weather.getCurrent("dew point"), "56");
    }

    public void testCanSeeCurrentLocation() {
        assertEquals("Should see the location in XML", weather.getLocation(), "Sunnyvale, CA");
    }

    public void testCanSeeAvailableForecasts() {
        Collection<String> availableForecasts = weather.getAvailableForecasts();
        List<String> expectedEntries = asList("k-p12h-n13-1", "k-p24h-n6-2", "k-p1h-n1-1", "k-p24h-n7-1");
        for(String each : availableForecasts) {
            assertTrue(each + " should be in expected entries",expectedEntries.contains(each));
        };
    }

    public void testCanFindLastForecast() {
        assertEquals("The last forecast should be determined by the size of -nxx", "k-p12h-n13-1",
                weather.lastForecast());
    }

    public void testCanSeeForecast() {
        List<Map<String, String>> weatherForecast = weather.getForecast("k-p12h-n13-1");
        int theSize = weatherForecast.size();
        assertTrue( "Should forecast for 13 days",theSize == 13 );
        assertForecasts(asList("Today", "Tonight", "Wednesday", "Wednesday Night", "Thursday"), "day");
        assertForecasts(asList("skc.png", "nskc.png", "few.png", "nbknfg.png", "sctfg.png"), "iconLink");
        assertForecasts(asList("Sunny", "Clear", "Sunny", "Patchy Fog", "Patchy Fog", "Mostly Clear"), "shortDescription");
        assertForecasts(asList("Sunny, with a high near 81. North northwest wind 3 to 8 mph. "), "description");
    }

    public void assertForecasts(List list, String key) {
        for (int idx = 0; idx < list.size(); idx++) {
            String each = (String) list.get(idx);
            String actual = weather.getForecast("k-p12h-n13-1").get(idx).get(key);
            assertTrue( idx + " Forecast should have key: " + key,null!=actual );
            assertTrue( idx + " Forecast should end with " + each + " but was " + actual,actual.endsWith(each) );
        }
    }
}
