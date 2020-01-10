package com.beac.beaconintegartion;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Locationupdates extends AppCompatActivity {


    GPSTracker locationUpdaterService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationupdates);
        locationUpdaterService=new GPSTracker(Locationupdates.this);
        if (locationUpdaterService.canGetLocation()) {

            double latitude = locationUpdaterService.getLatitude();
            double longitude = locationUpdaterService.getLongitude();

            double lat1=31.2985374;
            double lon1=75.5814861;
            float [] results1 = new float[5];
            Location.distanceBetween(lat1, lon1, latitude,
                    longitude, results1);

            Toast.makeText(Locationupdates.this, "Your Distance " + results1[0], Toast.LENGTH_LONG).show();
        } else {


            locationUpdaterService.showSettingsAlert();
        }
      //  Toast.makeText(locationUpdaterService, ""+locationUpdaterService.getLatitude(), Toast.LENGTH_SHORT).show();
       // Toast.makeText(Locationupdates.this, "hello", Toast.LENGTH_SHORT).show();
    }


}
