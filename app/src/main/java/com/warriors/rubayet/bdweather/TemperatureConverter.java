package com.warriors.rubayet.bdweather;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Rubayet on 01-Jan-16.
 */
public class TemperatureConverter
{
    String temp;
    String temp_max;
    String temp_min;

    public TemperatureConverter(String temp_max, String temp_min) {
        this.temp_max = temp_max;
        this.temp_min = temp_min;
    }

    public TemperatureConverter(String temp, String temp_max, String temp_min) {

        this.temp = temp;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
    }

    public String getTemp() {

        double tempF = Double.valueOf(temp);
        double tempC = (0.555*(tempF-32));
        NumberFormat formatter = new DecimalFormat("#0.00");

        String finalTemp = formatter.format(tempC);

        return finalTemp;
    }

    public String getTemp_max() {
        double tempF = Double.valueOf(temp_max);
        double tempC = (0.555*(tempF-32));
        NumberFormat formatter = new DecimalFormat("#0.00");

        String finalTemp_max = formatter.format(tempC);

        return finalTemp_max;
    }

    public String getTemp_min() {
        double tempF = Double.valueOf(temp_min);
        double tempC = (0.555*(tempF-32));
        NumberFormat formatter = new DecimalFormat("#0.00");

        String finalTemp_min = formatter.format(tempC);

        return finalTemp_min;
    }


}
