package com.beac.beaconintegartion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Listingactivity extends AppCompatActivity {
    Button viewrec, add_spots,scanbeacon,scan_route,remove_rec,kiosk_addgrp,search_grp;
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listingactivity);
        viewrec=findViewById(R.id.view_rec);
        add_spots=findViewById(R.id.add_spot);
        scanbeacon=findViewById(R.id.scanbeacon);
        scan_route=findViewById(R.id.scan_route);
        remove_rec=findViewById(R.id.remove_rec);
        kiosk_addgrp=findViewById(R.id.kiosk);
        search_grp=findViewById(R.id.search_grp);
        Realm.init(this);



        viewrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listingactivity.this, Viewrecords.class);
                startActivity(intent);
            }
        });

        search_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listingactivity.this, GroupFilter.class);
                startActivity(intent);
            }
        });

        remove_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Listingactivity.this);
                builder.setTitle("Are you sure you want to delete this?");

                //if the response is positive in the alert
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
                        Realm.setDefaultConfiguration(configuration);
                        Realm.getInstance(configuration);

                        Realm realmDB;
                        Realm.init(Listingactivity.this);
                        realmDB=Realm.getDefaultInstance();
                        try {
                            realmDB.beginTransaction();
                            realmDB.delete(Beaconspots_realm.class);
                            realmDB.delete(Spotattributes.class);
                            realmDB.commitTransaction();
                            Toast.makeText(Listingactivity.this, "All records are deleted.", Toast.LENGTH_SHORT).show();
                        }catch (Exception ex) {
                            Log.d("RError", "Error");
                        }


                    }
                });

                //if response is negative nothing is being done
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //creating and displaying the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();




            }
        });

        add_spots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listingactivity.this, Projectinfo.class);
                startActivity(intent);
            }
        });
        scanbeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listingactivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        kiosk_addgrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listingactivity.this, Addgroup.class);
                startActivity(intent);
            }
        });


        scan_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Listingactivity.this, Gpstrackinginfo.class);
                startActivity(intent);
            }
        });

    }
}
