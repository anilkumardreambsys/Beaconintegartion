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

public class Kioskadapter_result extends ArrayAdapter<Groupinfo_results> {
    List<Groupinfo_results> getresult;
    //activity context
    Context context;
    //the layout resource file for the list items
    int resource;
    Realm realm;

    //constructor initializing the values
    public Kioskadapter_result(Context context, int resource, List<Groupinfo_results> getresult) {
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
        TextView grpname = view.findViewById(R.id.grp_name);
        TextView grpid = view.findViewById(R.id.grp_id);
        TextView grppwd = view.findViewById(R.id.grp_pwd);
        TextView grpphone = view.findViewById(R.id.grp_phone);
        Button buttonDelete = view.findViewById(R.id.delete_btn);


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we will call this method to remove the selected value from the list
                //we are passing the position which is to be removed in the method
                removekiosk(getresult.get(position).getId(),position);
                //Toast.makeText(getContext(), "This is click. "+getresult.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });

        //getting the hero of the specified position
        Groupinfo_results rows = getresult.get(position);

        //adding values to the list item
        // imageView.setImageDrawable(context.getResources().getDrawable(hero.getImage()));
        //textViewName.setText(rows.getSpot_name());

        grpname.setText("Group Name = "+rows.getGroup_name());
        grpid.setText("ID = "+rows.getId());
        grppwd.setText("Password = "+rows.getGroup_pwd());
        grpphone.setText("Phone = "+rows.getGroup_phone());


        //adding a click listener to the button to remove item from the list


        //finally returning the view
        return view;
    }

    private void removekiosk(final int position, final int defaultpost) {
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
                final RealmResults<Groupinfo> students = realm
                        .where(Groupinfo.class)
                        .findAll();

                Groupinfo userdatabase = students
                        .where()
                        .equalTo("id",position)
                        .findFirst();

                if(userdatabase!=null) {

                    if (!realm.isInTransaction()) {
                        realm.beginTransaction();
                    }

                    userdatabase.deleteFromRealm();

                    realm.commitTransaction();
                    final RealmResults<Groupattr> students1 = realm
                            .where(Groupattr.class)
                            .findAll();

                    Groupattr Grpattributes = students1
                            .where()
                            .equalTo("groupid",position)
                            .findFirst();

                    if(Grpattributes!=null) {

                        if (!realm.isInTransaction()) {
                            realm.beginTransaction();
                        }
                        Grpattributes.deleteFromRealm();

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
