package com.diwakar.nath.clima;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /*
      URL = "api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}"
      http://api.openweathermap.org/data/2.5/weather?q=imphal&appid=a642481f8372246377b4ec26b341c6e4
     */

    // Declaring the URL and APP ID
    protected final String URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    protected final String APP_ID = "eec49385737ac85465454a2eda3de8e5";

    // LogCat TAG Name
    protected final String TAG = "Clima";

    // declaring member variable of the layout
    TextView mTemperature;
    TextView mWeatherCondition;
    TextView mCityName;
    ImageView mShowWeather;
    RelativeLayout mCityFinder;

    // declaring member variable for getting current location
    FusedLocationProviderClient fusedLocationProviderClient;
    double mlatitude, mlongitude;

    // declaring member variable
    int[] iconName = {
            R.drawable.cloudy2,
            R.drawable.dunno,
            R.drawable.fog,
            R.drawable.light_rain,
            R.drawable.overcast,
            R.drawable.snow4,
            R.drawable.tstorm3,
            R.drawable.tstorm1,
            R.drawable.sunny,
            R.drawable.shower3,
            R.drawable.snow5
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting Resource ID
        mTemperature = (TextView) findViewById(R.id.temperature);
        mWeatherCondition = (TextView) findViewById(R.id.weatherCondition);
        mCityName = (TextView) findViewById(R.id.cityName);
        mShowWeather = (ImageView) findViewById(R.id.showWeather);
        mCityFinder = (RelativeLayout) findViewById(R.id.cityFinder);

        // going from one activity to another (for getting weather info through city)
        mCityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, findCity.class);
                startActivity(intent);
            }
        });

        getCurrentLocation();
        getLocation();
        getWeatherDataForCurrentLocation();
    }

    protected void getWeatherDataForCurrentLocation() {
        /*
        http://api.openweathermap.org/data/2.5/weather?lat=25&lon=93&appid=eec49385737ac85465454a2eda3de8e5
         */
        String tempURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + mlatitude + "&lon=" + mlongitude + "&appid=" + APP_ID + "&units=metric";

        Log.d(TAG, "full link : " + tempURL);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempURL,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // TODO: Display the response
                        try {
                            // find temperature
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("main");
                            double temp = object.getDouble("temp");
                            mTemperature.setText(Double.toString(temp) + "°C");

                            // find city
                            String city = jsonObject.getString("name");
                            mCityName.setText(city);

                            // find weather condition
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            JSONObject obj = jsonArray.getJSONObject(0);
                            String weatherCondition = obj.getString("description");
                            mWeatherCondition.setText(weatherCondition);

                            // find id form the JSONArray above
                            int mCondition = obj.getInt("id");
                            Log.d(TAG, "id is : " + mCondition);

                            // find the icon
                            int iconID = updateWeatherIcon(mCondition);
                            mShowWeather.setImageResource(iconName[iconID]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handel error
                Log.d(TAG, "error : " + error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }






    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");

        if(cityName != null) {
            getWeatherForCity(cityName);
        }
        else {
            Toast.makeText(getApplicationContext(), "city name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    protected void getWeatherForCity(String cityName) {
        String tempURL = URL + cityName + "&appid=" + APP_ID + "&units=metric";
        Log.d(TAG, "full link : " + tempURL);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempURL,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // TODO: Display the response
                        try {
                            // find temperature
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("main");
                            double temp = object.getDouble("temp");
                            mTemperature.setText(Double.toString(temp) + "°C");

                            // find city
                            String city = jsonObject.getString("name");
                            mCityName.setText(city);

                            // find weather condition
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            JSONObject obj = jsonArray.getJSONObject(0);
                            String weatherCondition = obj.getString("description");
                            mWeatherCondition.setText(weatherCondition);

                            // find id form the JSONArray above
                            int mCondition = obj.getInt("id");
                            Log.d(TAG, "id is : " + mCondition);

                            // find the icon
                            int iconID = updateWeatherIcon(mCondition);
                            mShowWeather.setImageResource(iconName[iconID]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handel error
                Log.d(TAG, "error : " + error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        //---------------
        getCurrentLocation();
        getLocation();

    }

    // Get the weather image name from OpenWeatherMap's condition (marked by a number code)
    protected static int updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return 7;
        } else if (condition >= 300 && condition < 500) {
            return 3;
        } else if (condition >= 500 && condition < 600) {
            return 9;
        } else if (condition >= 600 && condition <= 700) {
            return 5;
        } else if (condition >= 701 && condition <= 771) {
            return 2;
        } else if (condition >= 772 && condition < 800) {
            return 6;
        } else if (condition == 800) {
            return 8;
        } else if (condition >= 801 && condition <= 804) {
            return 0;
        } else if (condition >= 900 && condition <= 902) {
            return 6;
        } else if (condition == 903) {
            return 10;
        } else if (condition == 904) {
            return 8;
        } else if (condition >= 905 && condition <= 1000) {
            return 6;
        }
        return 1;
    }

    protected void getCurrentLocation() {
        // initialized fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // check permission
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // when permission granted
            getLocation();
        } else {
            // when permission denied
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    protected void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initialized Location
                Location location = task.getResult();
                if(location != null) {
                    try {
                        // Initialized geocoder
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        // Initialized address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);

                        // getting the latitude and longitude
                        // Log.d(TAG, "Location : "+ addresses.toString());
                        mlatitude = addresses.get(0).getLatitude();
                        mlongitude = addresses.get(0).getLongitude();
                        Log.d(TAG, "latitude & longitude : "+ mlatitude + " , " + mlongitude);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}