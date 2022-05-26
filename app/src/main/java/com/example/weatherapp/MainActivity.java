package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_btn;
    private TextView result_info;
    TextView location_info;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);
        location_info = findViewById(R.id.location_info);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(lastKnownLocation == null){
                updateLocationInfo(lastKnownLocation);
            }
        }


        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, "Wpisz Miasto!", Toast.LENGTH_LONG).show();
                else {
                    String city = user_field.getText().toString();
                    String key = "ab66139de56ab2f7008294cd3a4cdf71";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=pl";

                    new GetURLData().execute(url);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void updateLocationInfo(Location location){
        //Log.i("Lokalizacja", location.toString());
        String Position = ("Szerokość geograficzna: " + "\n" +  location.getLatitude() + "\n" + "Długość geograficzna: " + "\n" + location.getLongitude());

        String info = "brakDanych";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
            if(addressList != null && addressList.size() > 0) {
                info = "informacje: \n";
                if(addressList.get(0).getThoroughfare() != null){
                    info += "Ulica:" + '\n' +addressList.get(0).getThoroughfare() +'\n';
                }
                if(addressList.get(0).getLocality() != null){
                    info += "Rejon:" + '\n' +addressList.get(0).getLocality() +'\n';
                }
                if(addressList.get(0).getPostalCode() != null){
                    info += "Kod pocztowy:" + '\n' + addressList.get(0).getPostalCode() +'\n';
                }
                if(addressList.get(0).getAdminArea() != null){
                    info += "Obszar:" + '\n' + addressList.get(0).getAdminArea() +'\n';
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        location_info.setText(Position + "\n" + info );
    }

    public void startListening(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }



    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ladowanie...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String temperature = "Temperatura: " + jsonObject.getJSONObject("main").getDouble("temp") + "°C";
                String feels = "Odczuwalne jak: " + jsonObject.getJSONObject("main").getDouble("feels_like") + "°C";
                String description = "Opis: " + jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String wind = "Wiatr: " + jsonObject.getJSONObject("wind").getDouble("speed") + " km/h";
                result_info.setText(description + "\n" + temperature + "\n" + feels + "\n" + wind);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}