package com.beac.beaconintegartion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {
    EditText user_id,userpwd;
    Button login_btn;
    String datauser_id, emailid,fname;
    SweetAlertDialog pDialog;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_id=findViewById(R.id.loginid);
        userpwd=findViewById(R.id.userpassword);
        login_btn=findViewById(R.id.lgn_btn);




        // Check if already login or not
        SharedPreferences checklogin=getSharedPreferences("user_details", MODE_PRIVATE);
        Boolean islogin = checklogin.getBoolean("is_login", false);
        //Toast.makeText(this, ""+islogin, Toast.LENGTH_SHORT).show();
        if(islogin){
            Intent intent=new Intent(this,Listingactivity.class);
            startActivity(intent);
            //Login.this.finish();
        }

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(MainActivity.this, "Hello Testing", Toast.LENGTH_SHORT).show();
                if(user_id.getText().toString().length()==0){
                    user_id.requestFocus();
                    Toast.makeText(Login.this, "Please enter your user id.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(userpwd.getText().toString().length()==0){
                    userpwd.requestFocus();
                    Toast.makeText(Login.this, "Please enter your password.", Toast.LENGTH_LONG).show();
                    return;
                }
                loginUser();
            }

            private void loginUser() {

                final String username=user_id.getText().toString().trim();
                final String password=userpwd.getText().toString().trim();


                Spinner mySpinner = (Spinner) findViewById(R.id.projectlist);
                String selectedproject = mySpinner.getSelectedItem().toString();


                pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading");
                pDialog.setCancelable(false);
                pDialog.show();
                Toast.makeText(Login.this, "hhh"+selectedproject, Toast.LENGTH_SHORT).show();
                if(username.equals("ajay") && password.equals("ajay@123")){
                    Toast.makeText(Login.this, "Loggedin successfully.", Toast.LENGTH_SHORT).show();
                    pDialog.hide();
                    SharedPreferences.Editor editor=getSharedPreferences("user_details",MODE_PRIVATE).edit();
                    editor.putBoolean("is_login",true);
                    editor.putString("projectname",selectedproject);
                    editor.apply();
                    Intent intent = new Intent(Login.this, Listingactivity.class);
                    startActivity(intent);
                    //Login.this.finish();
                }else{
                    //Toast.makeText(Login.this, "Invalid Username and password.", Toast.LENGTH_SHORT).show();
                    pDialog.hide();
                }
            }

            /*private void parseData(String response) {
                try {
                    //Toast.makeText(Login.this, "yes", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.d("Response", "" + response.toString());
                    if (jsonObject.getString("status").equals("true")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        datauser_id = data.getString("user_id");
                        emailid = data.getString("email");
                        fname = data.getString("first_name");

                        // Stored values in session that is shared preference
                        SharedPreferences.Editor editor=getSharedPreferences("user_details",MODE_PRIVATE).edit();
                        editor.putString("user_id",datauser_id);
                        editor.putString("alldetails",response.toString());
                        editor.putBoolean("is_login",true);
                        editor.apply();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        Login.this.finish();

                        //Toast.makeText(Login.this, "Loggedin successfully.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Login.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }*/
        });

    }



}
