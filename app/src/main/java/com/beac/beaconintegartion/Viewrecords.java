package com.beac.beaconintegartion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class Viewrecords extends AppCompatActivity {

    ListView listView;
    Realm realm;
    List<Spotresults> resultsinfo;
    Button backhome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecords);


        resultsinfo = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        backhome=findViewById(R.id.backhome);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Viewrecords.this, Listingactivity.class);
                startActivity(intent);
            }
        });

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        realm = Realm.getDefaultInstance();
        RealmResults<Beaconspots_realm> results = realm.where(Beaconspots_realm.class).findAll();
        results = results.sort("id", Sort.DESCENDING);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < results.size(); i++) {
            //stringBuilder.append(results.get(i).getSpot_name() + "  ");
            int spotid=results.get(i).getId();
            String getSpot_name=results.get(i).getSpot_name();
            String get_beaconid=results.get(i).getBeaconid();
            String get_lat=results.get(i).getLatitude();
            String get_long=results.get(i).getLongitude();
            String get_audio=results.get(i).getAudio_path();

            resultsinfo.add(new Spotresults(spotid,getSpot_name,get_beaconid,get_lat,get_long,get_audio,"sdadsdsa"));
          //  Toast.makeText(this, ""+results.get(i).getSpot_name(), Toast.LENGTH_SHORT).show();
        }
        //Log.d("RESSS",""+results);

        //resultsinfo.add(new Spotresults(1,"spot1","3242332342","8987","89787952","sdfsdfdsf","sdadsdsa"));
        //resultsinfo.add(new Spotresults(2,"spot2","3242332342","8987","89787952","sdfsdfdsf","sdadsdsa"));
       // resultsinfo.add(new Spotresults(3,"spot3","3242332342","8987","89787952","sdfsdfdsf","sdadsdsa"));
        //creating the adapter
        MyListAdapter adapter = new MyListAdapter(this, R.layout.spot_lists, resultsinfo);

        //attaching adapter to the listview
       listView.setAdapter(adapter);

    }


}
