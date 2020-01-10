package com.beac.beaconintegartion;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class KioskmemberRecord extends AppCompatActivity {
    Realm realm;
    TextView grp_name,grp_phone;
    LinearLayout get_frndname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kioskmember_record);

        grp_name=findViewById(R.id.grpname);
        grp_phone=findViewById(R.id.grpphn);
        get_frndname=findViewById(R.id.getfrndinfo);


        SharedPreferences filtersinfo=getSharedPreferences("group_filters", MODE_PRIVATE);
        String searchtype=filtersinfo.getString("searchtype", "");
        String grpphone=filtersinfo.getString("grphpone", "");
        String grppass=filtersinfo.getString("grp_password", "");
        String grpmember=filtersinfo.getString("grp_member", "");



        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        realm = Realm.getDefaultInstance();

        RealmResults<Groupinfo> results = realm.where(Groupinfo.class).equalTo("grp_phone",grpphone).findAll();
        int grpid = results.get(0).getId();
        String grpname = results.get(0).getGroup_name();
        String getgrpphone = results.get(0).getGrp_phone();

        RealmResults<Groupattr> grp_attrresults = realm.where(Groupattr.class).equalTo("groupid", grpid).findAll();
        //Toast.makeText(this, ""+grp_attrresults.size(), Toast.LENGTH_SHORT).show();
        int counter=1;
        for (int i = 0; i < grp_attrresults.size(); i++) {
            String frndname = grp_attrresults.get(i).getFriendname();

            TextView tv = new TextView(this); // Prepare textview object programmatically
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            tv.setText("" + counter+". "+frndname);
            tv.setId(i + 5);
            Button myButton = new Button(this);
            myButton.setText("Call");

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            get_frndname.addView(tv);
            get_frndname.addView(myButton, lp);
            counter++;
        }
        grp_name.setText("Group Name : "+grpname);
        grp_phone.setText("Group Phone : "+getgrpphone);

       // grpphone.setT


    }
}
