package com.beac.beaconintegartion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupFilter extends AppCompatActivity {

    RelativeLayout password_container,member_container;
    RadioGroup rg_grp;
    RadioButton rb_pwd,rb_memberinfo;
    Button search_btn,backbtn,viewbtn;
    EditText grp_pwd,member_info,grp_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_filter);
        password_container=findViewById(R.id.password_container);
        member_container=findViewById(R.id.member_container);
        grp_pwd=findViewById(R.id.enter_pwd);
        member_info=findViewById(R.id.member_info);
        grp_phone=findViewById(R.id.grpphone);

        search_btn=findViewById(R.id.search_btn);
        backbtn=findViewById(R.id.backbtn);
        viewbtn=findViewById(R.id.viewbtn);

        password_container.setVisibility(View.GONE);
        member_container.setVisibility(View.GONE);

        rb_pwd=findViewById(R.id.password);
        rb_memberinfo=findViewById(R.id.membermobile);

        rg_grp=findViewById(R.id.rg_container);
        rg_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);

                if(rb.getText().toString().equalsIgnoreCase("Password")){
                    password_container.setVisibility(View.VISIBLE);
                    member_container.setVisibility(View.GONE);
                }else{
                    password_container.setVisibility(View.GONE);
                    member_container.setVisibility(View.VISIBLE);
                }

            }
        });

        /*viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupFilter.this, Listingactivity.class);
                startActivity(intent);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupFilter.this, Listingactivity.class);
                startActivity(intent);
            }
        });*/

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewid=rg_grp.getCheckedRadioButtonId();
                if(viewid>0){
                    RadioButton radioButton = (RadioButton) findViewById(viewid);
                    String getinfo=radioButton.getText().toString();
                    if(getinfo.equals("Password")){
                        if(grp_pwd.getText().toString().length()==0){
                            Toast.makeText(GroupFilter.this, "Please enter password.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        if(member_info.getText().toString().length()==0){
                            Toast.makeText(GroupFilter.this, "Please enter Member phone or name.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }else{
                    Toast.makeText(GroupFilter.this, "Please select radio button", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(grp_phone.getText().toString().length()==0){
                    Toast.makeText(GroupFilter.this, "Please enter Group phone.", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton radioButton = (RadioButton) findViewById(viewid);
                String opttype=radioButton.getText().toString();
                String grphpone=grp_phone.getText().toString();
                String grp_password=grp_pwd.getText().toString();
                String grp_member=member_info.getText().toString();

                SharedPreferences.Editor editor=getSharedPreferences("group_filters",MODE_PRIVATE).edit();
                editor.putString("searchtype",opttype);
                editor.putString("grphpone",grphpone);
                editor.putString("grp_password",grp_password);
                editor.putString("grp_member",grp_member);
                editor.apply();

                Intent intent = new Intent(GroupFilter.this, KioskmemberRecord.class);
                startActivity(intent);

            }
        });

    }
}
