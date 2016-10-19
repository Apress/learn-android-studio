package com.apress.gerber.gradleweather;

import java.util.List;
import java.util.Map;

/**
 * Created by Clifton
 * Copyright 8/28/2014.
 */
public interface TemperatureData {
    List<TemperatureItem> getTemperatureItems();

    Map<String, String> getCurrentConditions();

    CharSequence getCity();
}
