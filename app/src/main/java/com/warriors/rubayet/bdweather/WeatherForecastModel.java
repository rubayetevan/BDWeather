package com.warriors.rubayet.bdweather;

/**
 * Created by Rubayet on 01-Jan-16.
 */
public class WeatherForecastModel {

    String date;
    String day;
    String tempHigh;
    String tempLow;
    String condition;

    public void setDate(String date) {
        this.date = date;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTempHigh(String tempHigh) {
        this.tempHigh = tempHigh;
    }

    public void setTempLow(String tempLow) {
        this.tempLow = tempLow;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public WeatherForecastModel() {

    }

    public WeatherForecastModel(String date, String day, String tempHigh, String tempLow, String condition) {

        this.date = date;
        this.day = day;
        this.tempHigh = tempHigh;
        this.tempLow = tempLow;
        this.condition = condition;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getTempHigh() {
        return tempHigh;
    }

    public String getTempLow() {
        return tempLow;
    }

    public String getCondition() {
        return condition;
    }
}
