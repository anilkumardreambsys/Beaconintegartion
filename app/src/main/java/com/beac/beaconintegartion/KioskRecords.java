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

public class KioskRecords extends AppCompatActivity {

    ListView listView;
    Realm realm;
    List<Groupinfo_results> resultsinfo;
    Button backhome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_records);


        resultsinfo = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        backhome=findViewById(R.id.backhome);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KioskRecords.this, Listingactivity.class);
                startActivity(intent);
            }
        });

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        realm = Realm.getDefaultInstance();
        RealmResults<Groupinfo> results = realm.where(Groupinfo.class).findAll();
        results = results.sort("id", Sort.DESCENDING);
        StringBuilder stringBuilder = new StringBuilder();

       for (int i = 0; i < results.size(); i++) {
            //stringBuilder.append(results.get(i).getSpot_name() + "  ");
            int grp_id=results.get(i).getId();
            String grp_name=results.get(i).getGroup_name();
            String grp_pwd=results.get(i).getGrp_password();
            String grp_phone=results.get(i).getGrp_phone();

            resultsinfo.add(new Groupinfo_results(grp_id,grp_name,grp_pwd,grp_phone));
            //  Toast.makeText(this, ""+results.get(i).getSpot_name(), Toast.LENGTH_SHORT).show();
        }
       Kioskadapter_result adapter = new Kioskadapter_result(this, R.layout.group_kioskresult, resultsinfo);

        //attaching adapter to the listview
        listView.setAdapter(adapter);

    }
}
