package com.beac.beaconintegartion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MyListAdapter extends ArrayAdapter<Spotresults> {
    //the list values in the List of type hero
    List<Spotresults> getresult;
    //activity context
    Context context;
    //the layout resource file for the list items
    int resource;
    Realm realm;

    //constructor initializing the values
    public MyListAdapter(Context context, int resource, List<Spotresults> getresult) {
        super(context, resource, getresult);
        this.context = context;
        this.resource = resource;
        this.getresult = getresult;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        //TextView textViewName = view.findViewById(R.id.textViewName);
        TextView spot_name = view.findViewById(R.id.spotname);
        TextView beaconid = view.findViewById(R.id.beaconid);
        TextView latitude = view.findViewById(R.id.latitude);
        TextView longitude = view.findViewById(R.id.longtitude);
        TextView spotid = view.findViewById(R.id.spotid);
        TextView audiopath = view.findViewById(R.id.audio);

        Button buttonDelete = view.findViewById(R.id.deletebtn);


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we will call this method to remove the selected value from the list
                //we are passing the position which is to be removed in the method
                removespot(getresult.get(position).getId(),position);
                //Toast.makeText(getContext(), "This is click. "+getresult.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });

        //getting the hero of the specified position

        Spotresults rows = getresult.get(position);

        spot_name.setText("Spot Name = "+rows.getSpot_name());
        spotid.setText("Spot ID = "+rows.getId());
        beaconid.setText("Beacon ID = "+rows.getBeaconid());
        latitude.setText("Latitude = "+rows.getLatitude());
        longitude.setText("Longitude ="+rows.getLongitude());
        audiopath.setText("Audio ="+rows.getAudio_path());

        //adding a click listener to the button to remove item from the list


        //finally returning the view
        return view;
    }

    private void removespot(final int position, final int defaultpost) {
        //Creating an alert dialog to confirm the deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        //if the response is positive in the alert
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //removing the item
                getresult.remove(defaultpost);
                //reloading the list
                notifyDataSetChanged();
                //Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();


                Realm.init(getContext());

                RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
                Realm.setDefaultConfiguration(configuration);
                Realm.getInstance(configuration);

                realm = Realm.getDefaultInstance();
                final RealmResults<Beaconspots_realm> students = realm
                        .where(Beaconspots_realm.class)
                        .findAll();

                Beaconspots_realm userdatabase = students
                        .where()
                        .equalTo("id",position)
                        .findFirst();

                if(userdatabase!=null) {

                    if (!realm.isInTransaction()) {
                        realm.beginTransaction();
                    }

                    userdatabase.deleteFromRealm();

                    realm.commitTransaction();
                    final RealmResults<Spotattributes> students1 = realm
                            .where(Spotattributes.class)
                            .findAll();

                    Spotattributes Spotattributes = students1
                            .where()
                            .equalTo("spotid",position)
                            .findFirst();

                    if(Spotattributes!=null) {

                        if (!realm.isInTransaction()) {
                            realm.beginTransaction();
                        }
                        Spotattributes.deleteFromRealm();

                        realm.commitTransaction();
                    }
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
}
