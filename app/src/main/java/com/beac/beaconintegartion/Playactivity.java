package com.beac.beaconintegartion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Playactivity extends AppCompatActivity {
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playactivity);
      /*  NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Anil_channel")
                .setSmallIcon(R.drawable.mandir)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);*/

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
                .setContentTitle("Hello Hriday")
                .setContentText("sadsadad")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        checkpermission();
    }
    public void buttonClicked(View view) throws FileNotFoundException {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.harsiddhi;
        //Toast.makeText(context, ""+uri, Toast.LENGTH_SHORT).show();
        VideoView mVideoView  = (VideoView) alertLayout.findViewById(R.id.videoview);
        if (mVideoView != null)
        {  mVideoView.setVideoURI(Uri.parse(uri));
            mVideoView.requestFocus();
            mVideoView.start();
        } else
        { //toast or print "mVideoView is null"
            //Toast.makeText(context, "Not found.", Toast.LENGTH_SHORT).show();
        }

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        realm = Realm.getDefaultInstance();
        RealmResults<Beaconspots_realm> results = realm.where(Beaconspots_realm.class).equalTo("id", 1).findAll();
       // results = results.sort("id", Sort.DESCENDING);
        int spotid=results.get(0).getId();
        String getSpot_name=results.get(0).getSpot_name();
        String get_beaconid=results.get(0).getBeaconid();
        String get_lat=results.get(0).getLatitude();
        String get_long=results.get(0).getLongitude();
        String get_audiopath=results.get(0).getAudio_path();
      //  String filepath = Environment.getExternalStorageDirectory().getPath();;
        Log.d("Audio_path",""+get_audiopath);

       // Log.d("phone_path",""+filepath);
       // Uri savedaudio = Uri.parse("content://com.android.providers.media.documents/document/audio:43627");
        //Toast.makeText(this, ""+get_audiopath, Toast.LENGTH_SHORT).show();
        Uri _uri = Uri.parse("content://com.android.providers.media.documents/document/audio:43627");
        Log.d("","URI = "+ _uri);
        String filePath = null;
        if (_uri != null && "content".equals(_uri.getScheme())) {
            Cursor cursor = this.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = _uri.getPath();
        }
        Log.d("","Chosen path = "+ filePath);
        //String filePath=PathUtil.getPath(getApplicationContext(),savedaudio);
        //Uri savedaudio = "content://com.android.providers.media.documents/document/audio:43627";
        //File file= FileUtils.getFile(savedaudio);
        //File file= FileUtils.getFile(savedaudio);
        //String path = _uri.getPath(context(),_uri);
        //openPath(_uri);

        //Log.d("","Chosen path = "+ path);

        /*MediaPlayer mPlayer = new MediaPlayer();
        //Uri myUri = Uri.parse("file:///sdcard/mp3/example.mp3");
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), savedaudio);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //StringBuilder stringBuilder = new StringBuilder();


        RealmResults<Spotattributes> spotresults = realm.where(Spotattributes.class).equalTo("spotid", spotid).findAll();
        for (int i = 0; i < spotresults.size(); i++) {
            int spotarr_id=spotresults.get(i).getSpotid();
            String getSpotarr_name=spotresults.get(i).getImage64_path();
           //Log.d("spotid",""+getSpotarr_name);
            LinearLayout layout = alertLayout.findViewById(R.id.img_gallery);
            ImageView image = new ImageView(this);
            image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            byte[] decodedString = Base64.decode(getSpotarr_name, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
            image.setImageBitmap(decodedByte);
            layout.addView(image);

        }


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getSpot_name);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        /*alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
              //  String user = etUsername.getText().toString();
                //String pass = etEmail.getText().toString();
            //    Toast.makeText(getBaseContext(), "Username: " + user + " Email: " + pass, Toast.LENGTH_SHORT).show();
            }
        });*/
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void checkpermission(){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

        ){
            ActivityCompat.requestPermissions(Playactivity.this,new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.INTERNET,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
        }
    }

}
