package com.apress.gerber.gradleweather;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clifton
 * Copyright 8/27/2014.
 */
public class ExampleTemperatureData implements TemperatureData {

    public static final String CURRENT = "apparent";
    public static final String LOW = "minimum";
    public static final String HIGH = "maximum";
    public static final String DEW_POINT = "dew point";
    private final Context context;

    public ExampleTemperatureData(Context context) {
        this.context = context;
    }

    @Override
    public List<TemperatureItem> getTemperatureItems() {
        List<TemperatureItem>items = new ArrayList<TemperatureItem>();
        items.add(new TemperatureItem(drawable(R.drawable.early_sunny),"Today", "Sunny",
                "Sunny, with a high near 81. North northwest wind 3 to 8 mph."));
        items.add(new TemperatureItem(drawable(R.drawable.night_clear), "Tonight", "Clear",
                "Clear, with a low around 59. North wind 5 to 10 mph becoming light northeast  in the evening."));
        items.add(new TemperatureItem(drawable(R.drawable.sunny_icon), "Wednesday", "Sunny",
                "Sunny, with a high near 82. North wind 3 to 8 mph."));
        items.add(new TemperatureItem(drawable(R.drawable.night_foggy), "Wednesday Night", "Patchy Fog",
                "Patchy fog after 2am.  Otherwise, mostly clear, with a low around 60. North wind 5 to 9 mph becoming calm  in the evening."));
        items.add(new TemperatureItem(drawable(R.drawable.day_foggy), "Thursday", "Patchy Fog",
                "Patchy fog before 8am.  Otherwise, sunny, with a high near 83. Calm wind becoming north around 5 mph in the afternoon."));
        items.add(new TemperatureItem(drawable(R.drawable.night_part_clear), "Thursday Night", "Mostly Clear",
                "Mostly clear, with a low around 60."));
        items.add(new TemperatureItem(drawable(R.drawable.sunny_icon), "Friday", "Sunny",
                "Sunny, with a high near 83."));
        items.add(new TemperatureItem(drawable(R.drawable.night_part_cloudy), "Friday Night", "Partly Cloudy",
                "Partly cloudy, with a low around 60."));
        items.add(new TemperatureItem(drawable(R.drawable.sunny_icon), "Saturday", "Mostly Sunny",
                "Mostly sunny, with a high near 82."));
        items.add(new TemperatureItem(drawable(R.drawable.night_part_clear), "Saturday Night", "Partly Cloudy",
                "Partly cloudy, with a low around 59."));
        items.add(new TemperatureItem(drawable(R.drawable.early_sunny), "Sunday", "Mostly Sunny",
                "Mostly sunny, with a high near 82."));
        items.add(new TemperatureItem(drawable(R.drawable.day_part_cloudy), "Sunday night", "Mostly Clear",
                "Mostly clear, with a low around 59."));
        items.add(new TemperatureItem(drawable(R.drawable.early_sunny), "Labor Day", "Sunny",
                "Sunny, with a high near 82."));
        return items;
    }

    private Drawable drawable(int resId) {
        return context.getResources().getDrawable(resId);
    }

    @Override
    public Map<String, String> getCurrentConditions() {
        Map<String, String> currentConditions = new HashMap<String, String>();
        currentConditions.put(CURRENT,"63");
        currentConditions.put(LOW,"59");
        currentConditions.put(HIGH,"81");
        currentConditions.put(DEW_POINT,"56");
        return currentConditions;
    }

    @Override
    public CharSequence getCity() {
        return "Sunnyvale";
    }
}
