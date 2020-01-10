package com.beac.beaconintegartion;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Projectinfo extends AppCompatActivity {

    RelativeLayout beaconconfig,gpsconfig;
    RadioGroup rg_systemselection;
    TextView headingtitle,state,info;
    ImageView audioicon;
    Button buttonPlay, gallerybtn,savebtn;

    List<String> imagesEncodedList;
    String imageEncoded;
    private GalleryAdapterStep1 galleryAdapter;
    private GridView gvGallery;
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    String encodedImage;
    RadioButton selectedRadioButton;
    EditText spotname,beaconid,latitude,longitude,audioinfo;

    MediaPlayer mp = new MediaPlayer();
    JSONObject jsonObject;
    MediaMetadataRetriever metaRetriever;
    Context context;
    List<String> targetList = new ArrayList<String>();
    Spinner audiolisting;
    //player.setAudioStreamType(AudioManager.STREAM_MUSIC);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectinfo);
        beaconconfig=findViewById(R.id.beacon_config);
        gpsconfig=findViewById(R.id.gps_config);
        state=findViewById(R.id.state);
        info=findViewById(R.id.info);
        //buttonPlay=findViewById(R.id.play);
        savebtn=findViewById(R.id.savebtn);

        audiolisting=findViewById(R.id.audiofiles);

        gallerybtn=findViewById(R.id.upload_galimg_step);

        beaconconfig.setVisibility(View.GONE);
        gpsconfig.setVisibility(View.GONE);
        gvGallery =findViewById(R.id.gv);

        //audioicon=findViewById(R.id.audiostart);




       // audioinfo=findViewById(R.id.audioinfo);

        MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        spotname=findViewById(R.id.spotname);
        beaconid=findViewById(R.id.beacon_id);
        latitude=findViewById(R.id.latitude);
        longitude=findViewById(R.id.longitude);

        /*audioicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,1);
            }
        });*/

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 2);
            }
        });

        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);
       /* Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

*/


        rg_systemselection=findViewById(R.id.systemselection);
        SharedPreferences pro_name=getSharedPreferences("user_details", MODE_PRIVATE);
        String projectname = pro_name.getString("projectname","");
        headingtitle=findViewById(R.id.project_name);
        headingtitle.setText("Project Name: "+projectname);
        checkpermission();
        rg_systemselection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=(RadioButton)findViewById(checkedId);

                if(rb.getText().toString().equalsIgnoreCase("Beacon")){
                    //Toast.makeText(Survey_step1.this, "Yes", Toast.LENGTH_SHORT).show();
                    //step1_upload.setVisibility(View.VISIBLE);
                    beaconconfig.setVisibility(View.VISIBLE);
                    gpsconfig.setVisibility(View.GONE);
                }else{
                    beaconconfig.setVisibility(View.GONE);
                    gpsconfig.setVisibility(View.VISIBLE);
                    //Toast.makeText(Survey_step1.this, "No", Toast.LENGTH_SHORT).show();
                    //step1_upload.setVisibility(View.GONE);
                }
                //Toast.makeText(Survey_step1.this, "-"+rb.getText()+"-", Toast.LENGTH_SHORT).show();
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Projectinfo.this, "Clicked on it.", Toast.LENGTH_SHORT).show();
                if(spotname.getText().toString().length()==0){
                    spotname.requestFocus();
                    Toast.makeText(Projectinfo.this, "Please enter spot name.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (rg_systemselection.getCheckedRadioButtonId() == -1)
                {
                    // no radio buttons are checked
                    Toast.makeText(Projectinfo.this, "Please select one of radio button.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    int selectedId = rg_systemselection.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectedRadioButton = (RadioButton)findViewById(selectedId);
                    //Toast.makeText(Projectinfo.this, selectedRadioButton.getText().toString()+" is selected", Toast.LENGTH_SHORT).show();
                    if(selectedRadioButton.getText().toString().equals("Beacon")){

                        if(beaconid.getText().toString().length()==0){
                            spotname.requestFocus();
                            Toast.makeText(Projectinfo.this, "Please enter beacon id.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else{
                        if(latitude.getText().toString().length()==0){
                            spotname.requestFocus();
                            Toast.makeText(Projectinfo.this, "Please enter Latitude.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(longitude.getText().toString().length()==0){
                            spotname.requestFocus();
                            Toast.makeText(Projectinfo.this, "Please enter Longitude.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    /*Realm realm = Realm.getDefaultInstance();realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Beacon_realm beacondata = realm.createObject(Beacon_realm.class);
                        beacondata.setSpot_name(spotname.getText().toString());
                        beacondata.setBeaconid(beaconid.getText().toString());
                        beacondata.setLatitude(latitude.getText().toString());
                        beacondata.setLongitude(longitude.getText().toString());
                        beacondata.setAudio_path(audioinfo.getText().toString());
                    }
                });*/

                    Realm realmDB;
                    Realm.init(Projectinfo.this);
                    realmDB=Realm.getDefaultInstance();
                    try{
                        realmDB.beginTransaction();
                        //realmDB.delete(Beaconspots_realm.class);
                        //realmDB.delete(Spotattributes.class);
                        //realmDB.commitTransaction();
                        Number maxId = realmDB.where(Beaconspots_realm.class).max("id");

                       final int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                        //Toast.makeText(Projectinfo.this, ""+nextId, Toast.LENGTH_SHORT).show();
                        String selectedproject = audiolisting.getSelectedItem().toString();
                        Beaconspots_realm spotinfo = realmDB.createObject(Beaconspots_realm.class,nextId);
                        spotinfo.setSpot_name(spotname.getText().toString());
                        spotinfo.setBeaconid(beaconid.getText().toString());
                        spotinfo.setLatitude(latitude.getText().toString());
                        spotinfo.setLongitude(longitude.getText().toString());
                        spotinfo.setAudio_path(selectedproject);
                        spotinfo.setImage_path("sdfsdfd");
                        realmDB.commitTransaction();
                        //Log.d("RSuccess","Success");
                        //Log.d("STATement",""+targetList.toString());


                        int spotid=nextId;

                        for (int i=0;i < targetList.size();i++)
                        {
                             Log.d("imgvalue",""+targetList.get(i));
                            //Log.i("Value of element "+i,myString.get(i));
                            realmDB.beginTransaction();
                            Number maxspotId = realmDB.where(Spotattributes.class).max("id");
                            int spotnextId = (maxspotId == null) ? 1 : maxspotId.intValue() + 1;
                            Spotattributes spotattr = realmDB.createObject(Spotattributes.class,spotnextId);
                            spotattr.setImage64_path(targetList.get(i));
                            spotattr.setSpotid(spotid);
                            realmDB.commitTransaction();
                        }
                        Intent intent = new Intent(Projectinfo.this, Viewrecords.class);
                        startActivity(intent);

                    }
                    catch (Exception ex) {
                        Log.d("RError", "Error");
                    }




                }
            }
        });
    }

    public void checkpermission(){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

        ){
            ActivityCompat.requestPermissions(Projectinfo.this,new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.INTERNET,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
            if (grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                checkpermission();
            }
        }
    }

    /*private boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }*/

    /*private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.INTERNET,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1000
        );
    }*/

    /*
       @Override    /* public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
           super.onRequestPermissionsResult(requestCode, permissions, grantResults);
           if (requestCode == 1000) {
               if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               }
               if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               }
               if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               } else {
                   //checkpermission();
               }
               if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               } else {
                   // checkpermission();
               }
               if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               } else {
                   //checkpermission();
               }
               if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               } else {
                   //checkpermission();
               }
               if (grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
               } else {
                   //checkpermission();
               }
           }

       }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (requestCode == 1 && resultCode == RESULT_OK) {
                    //Log.d("Testing123","sdffdsdfsdf"+imageFilePath);
                    if(data!=null)
                    {
                        Uri savedaudio = data.getData();

                        //String srcPath  = savedaudio.getPath();
                       // MediaPlayer mPlayer = new MediaPlayer();
                        //Uri myUri = Uri.parse("file:///sdcard/mp3/example.mp3");
                        //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        //audioinfo.setText(""+savedaudio);
                        /*try {
                            mPlayer.setDataSource(getApplicationContext(), savedaudio);
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        Log.d("debug" , "Record Path:" + savedaudio.getPath());
                    }
                }
                break;
            case 2:
                try {
                    // When an Image is picked
                    if (requestCode == 2 && resultCode == RESULT_OK
                            && null != data) {
                        // Get the Image from data
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        imagesEncodedList = new ArrayList<String>();
                        if(data.getData()!=null){

                            Uri mImageUri=data.getData();
                            //targetList.add(mImageUri.toString());

                            InputStream imageStream = getContentResolver().openInputStream(mImageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            encodedImage = encodeImage(selectedImage);
                            targetList.add(encodedImage);

                            // Get the cursor
                            Cursor cursor = getContentResolver().query(mImageUri,
                                    filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded  = cursor.getString(columnIndex);
                            cursor.close();

                           // ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            mArrayUri.add(mImageUri);
                            galleryAdapter = new GalleryAdapterStep1(getApplicationContext(),mArrayUri);
                            gvGallery.setAdapter(galleryAdapter);
                            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                    .getLayoutParams();
                            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);



                            //Toast.makeText(this, "Test1"+mImageUri, Toast.LENGTH_SHORT).show();

                        }else {
                            if (data.getClipData() != null) {
                                ClipData mClipData = data.getClipData();


                                for (int i = 0; i < mClipData.getItemCount(); i++) {

                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();

                                    mArrayUri.add(uri);
                                    //targetList.add(uri.toString());


                                    // for custom code
                                    InputStream imageStream = getContentResolver().openInputStream(uri);
                                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                    encodedImage = encodeImage(selectedImage);
                                    targetList.add(encodedImage);
                                    //image_data.add(encodedImage);

                                    // image_data.add("test1="+encodedImage);
                                    //end here

                                    // Get the cursor
                                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                    // Move to first row
                                    cursor.moveToFirst();

                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    imageEncoded  = cursor.getString(columnIndex);
                                    imagesEncodedList.add(imageEncoded);
                                    cursor.close();

                                    galleryAdapter = new GalleryAdapterStep1(getApplicationContext(),mArrayUri);
                                    gvGallery.setAdapter(galleryAdapter);
                                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                                            .getLayoutParams();
                                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                                }
                                 //new UploadImages().execute();
                                Log.d("LOG_TAG", "Selected Images" + mArrayUri.size());
                                //Log.d("Imageschecking", "Images" + image_data.toString());
                            }
                            //Toast.makeText(this, "Test2", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "You haven't picked Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }
                break;
       }
    }
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

}
