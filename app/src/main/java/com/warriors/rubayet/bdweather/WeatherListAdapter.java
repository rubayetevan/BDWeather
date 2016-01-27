package com.warriors.rubayet.bdweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rubayet on 01-Jan-16.
 */
public class WeatherListAdapter  extends ArrayAdapter{
    ArrayList<WeatherForecastModel> weatherForecastModels = new ArrayList<>();
    Context context;
    LayoutInflater inflater;

    public WeatherListAdapter(Context context, ArrayList<WeatherForecastModel> weatherForecastModels) {
        super(context, R.layout.forcast_list_view,weatherForecastModels);

        this.weatherForecastModels = weatherForecastModels;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder forecastHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.forcast_list_view, parent, false);
            forecastHolder.dayLTV = (TextView) convertView.findViewById(R.id.dayLTV);
            forecastHolder.dateLTV = (TextView) convertView.findViewById(R.id.dateLTV);
            forecastHolder.tempHighLTV = (TextView) convertView.findViewById(R.id.tempHighLTV);
            forecastHolder.tempLowLTV = (TextView) convertView.findViewById(R.id.tempLowLTV);
            forecastHolder.conditionLTV = (TextView) convertView.findViewById(R.id.conditionLTV);

            convertView.setTag(forecastHolder);
        }
        else
        {
            forecastHolder = (ViewHolder) convertView.getTag();
        }

        String day = (weatherForecastModels.get(position)).getDay();
        String date = (weatherForecastModels.get(position)).getDate();
        String tempH = (weatherForecastModels.get(position)).getTempHigh();
        String tempL = (weatherForecastModels.get(position)).getTempLow();
        String condition = (weatherForecastModels.get(position)).getCondition();

        TemperatureConverter temperatureConverterLV = new TemperatureConverter(tempH,tempL);

        forecastHolder.dayLTV.setText(day);
        forecastHolder.dateLTV.setText(date);
        forecastHolder.conditionLTV.setText(condition);
        forecastHolder.tempHighLTV.setText(temperatureConverterLV.getTemp_max()+"℃");
        forecastHolder.tempLowLTV.setText(temperatureConverterLV.getTemp_min()+"℃");

        return convertView;
    }

    public  static  class ViewHolder
    {
        TextView dayLTV;
        TextView dateLTV;
        TextView tempHighLTV;
        TextView tempLowLTV;
        TextView conditionLTV;
    }
}
