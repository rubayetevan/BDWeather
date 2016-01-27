package com.warriors.rubayet.bdweather;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Spinner divisionSpinner;
    Spinner zillaSpinner;
    String location;

    String urlO1="http://api.openweathermap.org/data/2.5/weather?q=";
    String urlO2=",BD&appid=2de143494c0b295cca9337e1e96b00e0";

    String urlY1="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
    String urlY2="%2C%20bd%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    ScrollView appSV;
    TextView tempTV;//
    TextView pressureTV;//
    TextView humidityTV;//
    TextView minTempTV;//
    TextView maxTempTV;//
    TextView windSpeedTV;
    TextView sunriseTV;//
    TextView sunsetTV;//
    TextView placeTV;//
    TextView locationTV;//
    TextView timeTV;
    TextView descriptionTV;
    ImageView iconIV;
    ListView forecastLV;
    SharedPreferences preferences;

    int clear = (R.drawable.sky_clear);
    int scatteredClouds = (R.drawable.scattered_clouds);
    int haze = (R.drawable.haze);
    int brokenClouds= (R.drawable.broken_clouds);
    int fewClouds = (R.drawable.few_clouds);
    int defaultIcon = (R.drawable.test);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewBYId();
        divisionSpinnerMethod();
        showData();
    }

    private void findViewBYId()
    {
        appSV = (ScrollView) findViewById(R.id.appSV);
        divisionSpinner = (Spinner) findViewById(R.id.divisionSpinner);
        zillaSpinner = (Spinner) findViewById(R.id.zillaSpinner);
        tempTV = (TextView) findViewById(R.id.tempTV);
        pressureTV = (TextView) findViewById(R.id.pressureTV);
        humidityTV = (TextView) findViewById(R.id.humidityTV);
        minTempTV = (TextView) findViewById(R.id.minTempTV);
        maxTempTV= (TextView) findViewById(R.id.maxTempTV);
        windSpeedTV = (TextView) findViewById(R.id.windSpeedTV);
        //windDirectionTV = (TextView) findViewById(R.id.windDirectionTV);
        sunriseTV = (TextView) findViewById(R.id.sunriseTV);
        sunsetTV = (TextView) findViewById(R.id.sunsetTV);
        placeTV = (TextView) findViewById(R.id.placeTV);
        locationTV = (TextView) findViewById(R.id.locationTV);
        timeTV = (TextView) findViewById(R.id.timeTV);
        descriptionTV = (TextView) findViewById(R.id.descriptionTV);
        iconIV = (ImageView) findViewById(R.id.iconIV);
        forecastLV = (ListView) findViewById(R.id.forecastLV);
    }
    private void getData(String urlY, String urlO)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlO, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String place = response.getString("name");
                    JSONObject mainObject = response.getJSONObject("main");
                    String temp = mainObject.getString("temp");
                    String pressure = mainObject.getString("pressure");
                    String humidity = mainObject.getString("humidity");
                    String temp_min = mainObject.getString("temp_min");
                    String temp_max = mainObject.getString("temp_max");
                    JSONObject windObject = response.getJSONObject("wind");
                    String speed =  windObject.getString("speed");
                    String deg =  windObject.getString("deg");
                    JSONObject sysObject = response.getJSONObject("sys");
                    String sunrise = sysObject.getString("sunrise");
                    String sunset = sysObject.getString("sunset");

                    JSONArray descriptionArray  = response.getJSONArray("weather");
                    JSONObject descriptionObject = descriptionArray.getJSONObject(0);
                    String condition = descriptionObject.getString("description");

                    //data conversion
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                    double tempC = Double.valueOf(temp) - 273;
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    String tempNew = String.valueOf(formatter.format(tempC));

                    preferences=getBaseContext().getSharedPreferences("weather", MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("place", place);
                    editor.putString("pressure", pressure);
                    editor.putString("humidity", humidity);
                    editor.putString("speed", speed);
                    editor.putString("temp", tempNew);
                    editor.putString("last_location",location);
                    editor.putString("last_time",currentDateTimeString);
                    editor.putString("condition", condition);
                    editor.apply();

                    tempTV.setText(tempNew+"℃");
                    pressureTV.setText(pressure+" hPa");
                    humidityTV.setText(humidity+" %");
                    windSpeedTV.setText(speed+" meter/sec");
                    placeTV.setText(place);
                    locationTV.setText(location);
                    timeTV.setText(currentDateTimeString);
                    descriptionTV.setText(condition.toUpperCase());

                    setIcon(condition);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();*/

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

        //////////////////////////////////////////////

        JsonObjectRequest yahooWeatherRequest =new JsonObjectRequest(Request.Method.GET, urlY, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject queryObject = response.getJSONObject("query");
                    JSONObject resultsObject = queryObject.getJSONObject("results");
                    JSONObject channelObject = resultsObject.getJSONObject("channel");
                    JSONObject itemObject = channelObject.getJSONObject("item");

                    JSONObject atmosphereObject = channelObject.getJSONObject("atmosphere");
                    //String humidity = atmosphereObject.getString("humidity");
                    //String pressure = atmosphereObject.getString("pressure");

                    JSONObject astronomyObject = channelObject.getJSONObject("astronomy");
                    String sunrise = astronomyObject.getString("sunrise");
                    String sunset = astronomyObject.getString("sunset");

                    JSONObject conditionObject = itemObject.getJSONObject("condition");
                    String temp = conditionObject.getString("temp");
                    //String condition = conditionObject.getString("text");

                    JSONArray forecastArray = itemObject.getJSONArray("forecast");
                    JSONObject day1Object = forecastArray.getJSONObject(0);
                    JSONObject day2Object = forecastArray.getJSONObject(1);
                    JSONObject day3Object = forecastArray.getJSONObject(2);
                    JSONObject day4Object = forecastArray.getJSONObject(3);
                    JSONObject day5Object = forecastArray.getJSONObject(4);

                    /////////////////

                    WeatherForecastModel day1model = new WeatherForecastModel();
                    WeatherForecastModel day2model = new WeatherForecastModel();
                    WeatherForecastModel day3model = new WeatherForecastModel();
                    WeatherForecastModel day4model = new WeatherForecastModel();
                    WeatherForecastModel day5model = new WeatherForecastModel();

                    day1model.setDate(day1Object.getString("date"));
                    day1model.setDay(day1Object.getString("day"));
                    day1model.setTempHigh(day1Object.getString("high"));
                    day1model.setTempLow(day1Object.getString("low"));
                    day1model.setCondition(day1Object.getString("text"));

                    day2model.setDate(day2Object.getString("date"));
                    day2model.setDay(day2Object.getString("day"));
                    day2model.setTempHigh(day2Object.getString("high"));
                    day2model.setTempLow(day2Object.getString("low"));
                    day2model.setCondition(day2Object.getString("text"));

                    day3model.setDate(day3Object.getString("date"));
                    day3model.setDay(day3Object.getString("day"));
                    day3model.setTempHigh(day3Object.getString("high"));
                    day3model.setTempLow(day3Object.getString("low"));
                    day3model.setCondition(day3Object.getString("text"));

                    day4model.setDate(day4Object.getString("date"));
                    day4model.setDay(day4Object.getString("day"));
                    day4model.setTempHigh(day4Object.getString("high"));
                    day4model.setTempLow(day4Object.getString("low"));
                    day4model.setCondition(day4Object.getString("text"));

                    day5model.setDate(day5Object.getString("date"));
                    day5model.setDay(day5Object.getString("day"));
                    day5model.setTempHigh(day5Object.getString("high"));
                    day5model.setTempLow(day5Object.getString("low"));
                    day5model.setCondition(day5Object.getString("text"));

                    ArrayList<WeatherForecastModel> weatherForecastModels = new ArrayList<>();
                    weatherForecastModels.add(0,day1model);
                    weatherForecastModels.add(1,day2model);
                    weatherForecastModels.add(2,day3model);
                    weatherForecastModels.add(3,day4model);
                    weatherForecastModels.add(4,day5model);

                    ///////////
                    String temp_max = day1Object.getString("high");
                    String temp_min = day1Object.getString("low");

                    JSONObject locationObject = channelObject.getJSONObject("location");
                    //String place = locationObject.getString("city");

                    JSONObject windObject = channelObject.getJSONObject("wind");
                    //String windSpeed = windObject.getString("speed");
                    //////
                    TemperatureConverter temperatureConverter = new TemperatureConverter(temp,temp_max,temp_min);
                    String temp_maxC = temperatureConverter.getTemp_max();
                    String temp_minC = temperatureConverter.getTemp_min();

                    preferences=getBaseContext().getSharedPreferences("weather", MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();

                    editor.putString("sunrise", sunrise);
                    editor.putString("sunset", sunset);
                    editor.putString("temp_max", temp_maxC);
                    editor.putString("temp_min", temp_minC);
                    editor.apply();

                    minTempTV.setText(temp_minC+"℃");
                    maxTempTV.setText(temp_maxC+"℃");
                    sunriseTV.setText(sunrise);
                    sunsetTV.setText(sunset);


                    WeatherListAdapter forecastListAdapter = new WeatherListAdapter(getApplicationContext(),weatherForecastModels);
                    forecastLV.setAdapter(forecastListAdapter);


                    Toast.makeText(getApplicationContext(),"Weather Data Updated Successfully!",Toast.LENGTH_SHORT).show();





                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(yahooWeatherRequest);

    }
    private void setIcon(String condition)
    {
        if(condition.equalsIgnoreCase("Sky is clear"))
        {
            iconIV.setImageResource(clear);
            appSV.setBackgroundResource(R.drawable.skyy);
        }
        else if(condition.equalsIgnoreCase("scattered clouds"))
        {
            iconIV.setImageResource(scatteredClouds);
            appSV.setBackgroundResource(R.drawable.scattered_cloudss);
        }
        else if(condition.equalsIgnoreCase("haze"))
        {
            iconIV.setImageResource(haze);
            appSV.setBackgroundResource(R.drawable.hazee);
        }
        else if(condition.equalsIgnoreCase("broken clouds"))
        {
            iconIV.setImageResource(brokenClouds);
            appSV.setBackgroundResource(R.drawable.broken_cloudss);
        }
        else if(condition.equalsIgnoreCase("few clouds"))
        {
            iconIV.setImageResource(fewClouds);
            appSV.setBackgroundResource(R.drawable.few_cloudss);
        }
        else
        {
            iconIV.setImageResource(defaultIcon);
            appSV.setBackgroundResource(R.drawable.defaultt);
        }


    }
    private void showData()
    {
        preferences=getBaseContext().getSharedPreferences("weather", MODE_PRIVATE);
        String place =  preferences.getString("place","");
        String pressure =  preferences.getString("pressure","");
        String humidity =  preferences.getString("humidity","");
        String speed =  preferences.getString("speed","");
        String timeSunrise =  preferences.getString("sunrise","");
        String timeSunset =  preferences.getString("sunset","");
        String tempNew =  preferences.getString("temp","");
        String tempCmxNew =  preferences.getString("temp_max","");
        String tempCmnNew =  preferences.getString("temp_min","");
        String lastLocation =  preferences.getString("last_location","");
        String currentDateTimeString = preferences.getString("last_time", "");
        String condition = preferences.getString("condition", "");

        tempTV.setText(tempNew+"℃");
        pressureTV.setText(pressure+" hPa");
        humidityTV.setText(humidity+" %");
        minTempTV.setText(tempCmnNew+"℃");
        maxTempTV.setText(tempCmxNew+"℃");
        windSpeedTV.setText(speed+" meter/sec");
        descriptionTV.setText(condition.toUpperCase());
        //windDirectionTV.setText(deg+"°");
        sunriseTV.setText(timeSunrise);
        sunsetTV.setText(timeSunset);
        placeTV.setText(place);
        locationTV.setText(lastLocation);
        timeTV.setText(currentDateTimeString);
        setIcon(condition);
    }
   private void divisionSpinnerMethod()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.division_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(adapter);

        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String division = (String) parent.getItemAtPosition(position);
                        zillaSpinnerMethod(division);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void zillaSpinnerMethod(String division)
      {
          if(division.equalsIgnoreCase("dhaka"))
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.dhaka_zilla_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            zillaSpinner.setAdapter(adapter);

            zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String zillaName = (String) parent.getItemAtPosition(position);
                    location = zillaName;
                    String urlY = urlY1 + location + urlY2;
                    String urlO = urlO1 + location + urlO2;
                    getData(urlY,urlO);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
         else if(division.equalsIgnoreCase("Barisal"))
          {
              ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                      R.array.barisal_zilla_array, android.R.layout.simple_spinner_item);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              zillaSpinner.setAdapter(adapter);

              zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      String zillaName = (String) parent.getItemAtPosition(position);
                      location = zillaName;
                      String urlY = urlY1 + location + urlY2;
                      String urlO = urlO1 + location + urlO2;
                      getData(urlY,urlO);
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
          }
          else if(division.equalsIgnoreCase("Khulna"))
          {
              ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                      R.array.khulna_zilla_array, android.R.layout.simple_spinner_item);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              zillaSpinner.setAdapter(adapter);

              zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      String zillaName = (String) parent.getItemAtPosition(position);
                      location = zillaName;
                      String urlY = urlY1 + location + urlY2;
                      String urlO = urlO1 + location + urlO2;
                      getData(urlY,urlO);
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
          }
          else if(division.equalsIgnoreCase("Rajshahi"))
          {
              ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                      R.array.rajshahi_zilla_array, android.R.layout.simple_spinner_item);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              zillaSpinner.setAdapter(adapter);

              zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      String zillaName = (String) parent.getItemAtPosition(position);
                      location = zillaName;
                      String urlY = urlY1 + location + urlY2;
                      String urlO = urlO1 + location + urlO2;
                      getData(urlY,urlO);
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
          }
          else if(division.equalsIgnoreCase("Sylhet"))
          {
              ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                      R.array.sylhet_zilla_array, android.R.layout.simple_spinner_item);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              zillaSpinner.setAdapter(adapter);

              zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      String zillaName = (String) parent.getItemAtPosition(position);
                      location = zillaName;
                      String urlY = urlY1 + location + urlY2;
                      String urlO = urlO1 + location + urlO2;
                      getData(urlY,urlO);
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
          }
          else if(division.equalsIgnoreCase("Chittagong"))
          {
              ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                      R.array.chittagong_zilla_array, android.R.layout.simple_spinner_item);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              zillaSpinner.setAdapter(adapter);

              zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      String zillaName = (String) parent.getItemAtPosition(position);
                      location = zillaName;
                      String urlY = urlY1 + location + urlY2;
                      String urlO = urlO1 + location + urlO2;
                      getData(urlY,urlO);
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
          }
          else if(division.equalsIgnoreCase("Rangpur"))
          {
              ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                      R.array.rangpur_zilla_array, android.R.layout.simple_spinner_item);
              adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              zillaSpinner.setAdapter(adapter);

              zillaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      String zillaName = (String) parent.getItemAtPosition(position);
                      location = zillaName;
                      String urlY = urlY1 + location + urlY2;
                      String urlO = urlO1 + location + urlO2;
                      getData(urlY,urlO);
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

                  }
              });
          }

    }
    public void onClickUpdate(View view)
    {
        preferences=getBaseContext().getSharedPreferences("weather", MODE_PRIVATE);
        String lastLocation =  preferences.getString("last_location","");
        String urlY = urlY1 + lastLocation + urlY2;
        String urlO = urlO1 + lastLocation + urlO2;

        getData(urlY,urlO);

    }




}
