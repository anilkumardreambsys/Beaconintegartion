package com.beac.beaconintegartion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Gpstrackinginfo extends AppCompatActivity {

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    GPSTracker gpsTracker;
    MediaPlayer mp = new MediaPlayer();
    Realm realm;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstrackinginfo);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //getLastLocation();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        realm = Realm.getDefaultInstance();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
               // readWebpage();
               requestNewLocationData();
                //gpstracker_data();
                //Toast.makeText(Gpstrackinginfo.this, "sdasasda", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, 4000); //now is every 2 minutes
            }
        }, 6000); //Every 120000 ms (2 minutes)
        gpsTracker = new GPSTracker(Gpstrackinginfo.this);
        if (gpsTracker.canGetLocation()) {

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();


            Toast.makeText(Gpstrackinginfo.this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {


            gpsTracker.showSettingsAlert();
        }

        /*if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude());
            Log.d("Getresponse",""+stringLatitude);

            String stringLongitude = String.valueOf(gpsTracker.longitude);
            Log.d("Getresponse",""+stringLongitude);

            String country = gpsTracker.getCountryName(this);
            Log.d("Getresponse",""+stringLongitude);

            String city = gpsTracker.getLocality(this);
            Log.d("Getresponse",""+city);

            String postalCode = gpsTracker.getPostalCode(this);
            Log.d("Getresponse",""+postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
            Log.d("Getresponse",""+addressLine);
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }*/
    }

    public void gpstracker_data(){
        gpsTracker = new GPSTracker(Gpstrackinginfo.this);
        if (gpsTracker.canGetLocation()) {

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();


            Toast.makeText(Gpstrackinginfo.this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {


            gpsTracker.showSettingsAlert();
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    Log.d("RESponse","ssdsdds");
                                    Toast.makeText(Gpstrackinginfo.this, ""+location.getLatitude(), Toast.LENGTH_SHORT).show();
                                    //latTextView.setText(location.getLatitude()+"");
                                   // lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();



            //ufodevice.getEddystoneInstance();
            // Date currentTime = Calendar.getInstance().getTime();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            RealmResults<Beaconspots_realm> results = realm.where(Beaconspots_realm.class).equalTo("Beaconid","").findAll();
            int counterresult = results.size();


            //Toast.makeText("", ""+counterresult, Toast.LENGTH_SHORT).show();
            //Toast.makeText(Gpstrackinginfo.this, "" +counterresult, Toast.LENGTH_SHORT).show();
            Double mlat=mLastLocation.getLatitude();
            Double mlong=mLastLocation.getLongitude();

            String output = "";
           // Toast.makeText(Gpstrackinginfo.this, mlat+","+mlong, Toast.LENGTH_SHORT).show();
            if (counterresult > 0) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
                for (int i = 0; i < results.size(); i++) {

                    int spotid=results.get(i).getId();
                    final String get_lat=results.get(i).getLatitude();
                    final String get_long=results.get(i).getLongitude();
                    String getSpot_name = results.get(i).getSpot_name();
                    double lat2 = Double.parseDouble(get_lat);
                    double long2 = Double.parseDouble(get_long);
                    //Toast.makeText(Gpstrackinginfo.this, "Lat="+get_lat+" Long="+get_long, Toast.LENGTH_SHORT).show();
                   // double distanceget=distance(lat2,long2,mlat,mlong);
                    float [] results1 = new float[5];
                    Location.distanceBetween(lat2, long2, mlat,
                            mlong, results1);




                    Log.d("Distance","Distance is="+results1[0]);
                    output += getSpot_name+" = "+results1[0]+"\n";
                    Toast.makeText(Gpstrackinginfo.this, output, Toast.LENGTH_SHORT).show();
                    if(results1[0]<=50){
                        String filename = "android.resource://" + getPackageName() + "/"+R.raw.wel;
                        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(Gpstrackinginfo.this);
                        String forlatlng = preferences1.getString(get_lat+""+get_long, "");
                        Boolean isgps = preferences1.getBoolean("check_gps_"+get_lat+""+get_long,true);


                        VideoView mVideoView = (VideoView) alertLayout.findViewById(R.id.videoview);
                        String uri = "android.resource://com.beac.beaconintegartion/" + R.raw.harsiddhi;
                        if (mVideoView != null) {
                            mVideoView.setVideoURI(Uri.parse(uri));
                            // mVideoView.requestFocus();
                            mVideoView.start();
                        } else { //toast or print "mVideoView is null"
                            //Toast.makeText(context, "Not found.", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Spotid",""+spotid);
                        RealmResults<Spotattributes> spotresults = realm.where(Spotattributes.class).equalTo("spotid", spotid).findAll();
                        for (int is = 0; is < spotresults.size(); is++) {
                            int spotarr_id = spotresults.get(is).getSpotid();
                            String getSpotarr_name = spotresults.get(is).getImage64_path();
                            //Log.d("spotid",""+getSpotarr_name);
                            LinearLayout layout = alertLayout.findViewById(R.id.img_gallery);
                            ImageView image = new ImageView(Gpstrackinginfo.this);
                            image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            byte[] decodedString = Base64.decode(getSpotarr_name, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            image.setImageBitmap(decodedByte);
                            layout.addView(image);

                        }

                        String toyBornTime = forlatlng;
                        SimpleDateFormat dateFormat = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");

                        try {

                            Date oldDate = dateFormat.parse(toyBornTime);
                            //System.out.println(oldDate);

                            Date currentDate1 = new Date();
                            //Toast.makeText(context, ""+currentDate1.getTime(), Toast.LENGTH_SHORT).show();

                            long diff = currentDate1.getTime() - oldDate.getTime();
                            long seconds = diff / 1000;
                            long minutes = seconds / 60;
                            long hours = minutes / 60;
                            long days = hours / 24;

                            if (oldDate.before(currentDate1)) {

                                //Log.e("oldDate", "is previous date");
                                Log.d("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                                        + " hours: " + hours + " days: " + days);
                                if(minutes>=1){
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Gpstrackinginfo.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(get_lat+""+get_long, currentDate);
                                    editor.putBoolean("check_gps_"+get_lat+""+get_long, true);
                                    editor.apply();
                                }


                            }

                            // Log.e("toyBornTime", "" + toyBornTime);

                                } catch (ParseException e) {

                            e.printStackTrace();
                            }
                            AlertDialog.Builder alert = new AlertDialog.Builder(Gpstrackinginfo.this);
                            alert.setTitle(getSpot_name);
                            // this is set the view from XML inside AlertDialog
                            alert.setView(alertLayout);
                            // disallow cancel of AlertDialog on click of back button and outside touch
                            alert.setCancelable(false);
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Gpstrackinginfo.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("check_gps_"+get_lat+""+get_long, false);
                                    editor.apply();
                                    //dialog.show();
                                }
                            });
                            AlertDialog dialog = alert.create();

                           if(isgps) {
                                dialog.show();
                                mp.reset();
                                try {
                                    mp.setDataSource(Gpstrackinginfo.this, Uri.parse(filename));
                                    mp.prepare();
                                    mp.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Gpstrackinginfo.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(get_lat+""+get_long, currentDate);
                                editor.putBoolean("check_gps_"+get_lat+""+get_long, false);
                                editor.apply();
                            }
                    }
                }

               // Toast.makeText(gpsTracker, ""+counterresult, Toast.LENGTH_SHORT).show();
            }
            //latTextView.setText(mLastLocation.getLatitude()+"");
            //lonTextView.setText(mLastLocation.getLongitude()+"");
           // Toast.makeText(Gpstrackinginfo.this, ""+mLastLocation.getLatitude()+" "+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();

            //Log.d("RESponse","dsdsd");
        }
    };



    private void distance(double lat1, double lon1, double lat2, double lon2) {
       /* double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 *1000;
        return (dist);*/

       // Toast.makeText(gpsTracker, "", Toast.LENGTH_SHORT).show();
        //return dist;
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


        /*Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(long2);

        double distance=startPoint.distanceTo(endPoint);
        return distance;*/


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

}
