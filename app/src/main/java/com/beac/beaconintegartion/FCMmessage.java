package com.beac.beaconintegartion;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class FCMmessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmmessage);
        String deviceAppUID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Device id",""+deviceAppUID);

        //Toast.makeText(this, ""+deviceAppUID, Toast.LENGTH_SHORT).show();

        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default_notification_channel_id")
                .setContentTitle("Location")
                .setContentText("This is test app")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());*/
    }
}
