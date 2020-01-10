package com.beac.beaconintegartion;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Addgroup extends AppCompatActivity {

    Button btnMyLine,btnSave,viewrec_btn;
    LinearLayout LLEnterText;
    int _intMyLineCount;

    private List<EditText> editTextList_phone = new ArrayList<EditText>();
    private List<EditText> editTextList_name = new ArrayList<EditText>();
    private List<TextView> textviewList=new ArrayList<TextView>();
    private List<Button> buttonviewList=new ArrayList<Button>();
    private List<LinearLayout> linearlayoutList=new ArrayList<LinearLayout>();
    LinearLayout.LayoutParams layoutarams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgroup);

        LLEnterText=(LinearLayout) findViewById(R.id.editext_container);
        btnMyLine=(Button) findViewById(R.id.saveline);
        btnSave=(Button) findViewById(R.id.savebtn);

        viewrec_btn=(Button) findViewById(R.id.viewbtn);

        btnMyLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLEnterText.addView(linearlayout(_intMyLineCount));
                _intMyLineCount++;
            }
        });


        viewrec_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Addgroup.this, KioskRecords.class);
                startActivity(intent);
            }
        });

        final EditText grp_name,grp_phone,grp_pwd;
        grp_name=findViewById(R.id.groupname);
        grp_phone=findViewById(R.id.groupphone);
        grp_pwd=findViewById(R.id.password);

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(Addgroup.this, "" + linearlayoutList.size(), Toast.LENGTH_SHORT).show();
                Log.d("Stringdata", "" + linearlayoutList);
                int count = linearlayoutList.size();
                if(count==0){
                    Toast.makeText(Addgroup.this, "Please add records.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(grp_name.getText().toString().length()==0){
                    Toast.makeText(Addgroup.this, "Please enter Group name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(grp_pwd.getText().toString().length()==0){
                    Toast.makeText(Addgroup.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(grp_phone.getText().toString().length()==0){
                    Toast.makeText(Addgroup.this, "Please enter Phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (count > 0){
                    //LinearLayout layout = setupLayout();
                    Realm realmDB;
                    Realm.init(Addgroup.this);
                    realmDB = Realm.getDefaultInstance();
                    try {
                        realmDB.beginTransaction();
                        Number maxId = realmDB.where(Groupinfo.class).max("id");
                        final int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                        Groupinfo groupinfo = realmDB.createObject(Groupinfo.class, nextId);
                        groupinfo.setGroup_name(grp_name.getText().toString());
                        groupinfo.setGrp_password(grp_pwd.getText().toString());
                        groupinfo.setGrp_phone(grp_phone.getText().toString());
                        realmDB.commitTransaction();

                        int attrid = nextId;
                        for (int i = 0; i < editTextList_name.size(); i++) {
                            Log.d("Name ", "Val " + editTextList_name.get(i).getText().toString());
                            Log.d("Mobile ", "Val " + editTextList_phone.get(i).getText().toString());

                            realmDB.beginTransaction();
                            Number maxspotId = realmDB.where(Groupattr.class).max("id");
                            int grpnextId = (maxspotId == null) ? 1 : maxspotId.intValue() + 1;
                            Groupattr grpattr = realmDB.createObject(Groupattr.class, grpnextId);
                            grpattr.setGroupid(attrid);
                            grpattr.setFriendname(editTextList_name.get(i).getText().toString());
                            grpattr.setFriendmobile(editTextList_phone.get(i).getText().toString());
                            realmDB.commitTransaction();
                        }
                        Intent intent = new Intent(Addgroup.this, KioskRecords.class);
                        startActivity(intent);

                    } catch (Exception ex) {
                        Log.d("RError", "Error");
                    }
               }
            }
        });

    }

    private EditText friendmobile(int _intID) {
        EditText editText = new EditText(this);
        editText.setId(_intID);
        editText.setHint("Mobile Numb");
        //editText.setBackgroundColor(Color.WHITE);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editTextList_phone.add(editText);
        return editText;
    }

    private EditText friendname(int _intID) {
        EditText editText = new EditText(this);
        editText.setId(_intID);
        editText.setHint("Friend Name");
        //editText.setBackgroundColor(Color.WHITE);
        //editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editTextList_name.add(editText);
        return editText;
    }

    private Button btndelete(int _intID)
    {
        Button deletebtnspecific=new Button(this);
        deletebtnspecific.setId(_intID);
        deletebtnspecific.setText("Delete");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
       // deletebtnspecific.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        deletebtnspecific.setTextColor(Color.WHITE);
        deletebtnspecific.setLeft(15);
        deletebtnspecific.setLayoutParams(params);

        deletebtnspecific.setBackgroundColor(Color.RED);
        buttonviewList.add(deletebtnspecific);
        final int id_ = deletebtnspecific.getId();
        deletebtnspecific.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LinearLayout linearParent =  (LinearLayout) view.getParent().getParent();
                LinearLayout linearChild = (LinearLayout) view.getParent();
                linearParent.removeView(linearChild);
            }
        });
        return deletebtnspecific;
    }
    private LinearLayout linearlayout(int _intID)
    {
        LinearLayout LLMain=new LinearLayout(this);
        LLMain.setId(_intID);
        //LLMain.addView(textView(_intID));
        LLMain.addView(friendname(_intID));
        LLMain.addView(friendmobile(_intID));

        LLMain.addView(btndelete(_intID));
        LLMain.setOrientation(LinearLayout.HORIZONTAL);
        LLMain.setBottom(20);
        linearlayoutList.add(LLMain);
        return LLMain;

    }
}
