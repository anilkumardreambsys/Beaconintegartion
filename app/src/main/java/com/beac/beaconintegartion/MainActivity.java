package com.beac.beaconintegartion;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.youtube.player.YouTubePlayerView;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnRangingListener;
import com.ufobeaconsdk.callback.OnScanSuccessListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.RangeType;
import com.ufobeaconsdk.main.UFOBeaconManager;
import com.ufobeaconsdk.main.UFODevice;
import com.ufobeaconsdk.main.UFODeviceType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvMenu, tvStopScan;
    RelativeLayout rel_StartScan;
    UfoBeaconAdapter ufoAdapter;
    ListView lvufoBeacon;
    Context context;
    private RelativeLayout relMenu;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<UFODevice> ufoDevicesList = new ArrayList<>();

    public static final int REQUEST_ENABLE_BT = 1000;

    PopupWindow popupWindow;
    RelativeLayout relativeLayout;

    UFOBeaconManager ufoBeaconManager;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean isFirstTimeLoad = false;

    private HandlerThread sortingTimerHandlerThread = null;
    private Handler sortingTimerHandler;
    private Button mButtonPlay,closePopupBtn;
    private Context mContext;
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MediaController media_control;
    VideoView myVideo;
    Realm realm;

    VideoView vid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ufoBeaconManager = new UFOBeaconManager(MainActivity.this);
        initScreen();
        listneScreen();
        isFirstTimeLoad = true;
        ufoAdapter = new UfoBeaconAdapter(context, lvufoBeacon);
        lvufoBeacon.setAdapter(ufoAdapter);
        //Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
        //startScan();
        relativeLayout = (RelativeLayout) findViewById(R.id.relaive1);
        //mButtonPlay = findViewById(R.id.btn_play);
      //  mButtonPlay = findViewById(R.id.btn_play);
        rel_StartScan = (RelativeLayout) findViewById(R.id.rel_bottom_ufobeacon);
        rel_StartScan.setVisibility(View.GONE);
        verifyBluetoothState();

        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);
        realm = Realm.getDefaultInstance();

    }











    private void initScreen() {
        context = MainActivity.this;
        swipeContainer = (SwipeRefreshLayout)
                findViewById(R.id.swipeContainer);
        lvufoBeacon = (ListView) findViewById(R.id.lvufoBeacon);
        relMenu = (RelativeLayout) findViewById(R.id.homescreen_rel_menu);
        tvMenu = (TextView) findViewById(R.id.tvMenu);
        tvStopScan = (TextView) findViewById(R.id.tvStopscan);
        tvStopScan.setVisibility(View.GONE);
        rel_StartScan = (RelativeLayout) findViewById(R.id.rel_bottom_ufobeacon);
        ufoBeaconManager = new UFOBeaconManager(MainActivity.this);
        if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 23) {
            ufoBeaconManager.isLocationServiceEnabled(new OnSuccessListener() {
                @Override
                public void onSuccess(boolean isSuccess) {
                    //verifyBluetoothState();
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(int code, String message) {
                    //Utililty.ShowDialogToStartLocation(UFOBeaconActivity.this);
                }
            });
        }

        lvufoBeacon.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (lvufoBeacon == null || lvufoBeacon.getChildCount() == 0) ? 0 : lvufoBeacon.getChildAt(0).getTop();
                swipeContainer.setEnabled((topRowVerticalPosition >= 0));
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (ufoBeaconManager != null && ufoBeaconManager.isScanRunning() && !isFirstTimeLoad) {
            stopScan();
            scanButtonVisible();
            startScan();
        }
        if (isFirstTimeLoad)
            isFirstTimeLoad = false;
        startSortingTimer();
    }
    // Print Toast message
    public void generateToast(int code, String message) {
        Toast.makeText(MainActivity.this, "code:- " + code + " - " + message, Toast.LENGTH_SHORT).show();
    }

    // Print Toast message
    public void generateSuccessToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void scanButtonVisible(){
        rel_StartScan.setVisibility(View.VISIBLE);
    }
    // Start scanning of BLE devices
    private void startScan() {
        if (ufoBeaconManager != null) {
            ufoBeaconManager.startScan(new OnScanSuccessListener() {
                @Override
                public void onSuccess(final UFODevice ufodevice) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //generateSuccessToast("Success");
                            //Log.d("UfoBeaconActivity", "Scanning updated " + ufodevice.getRssi() + " Mac = " + ufodevice.getBtdevice().getAddress());
                            swipeContainer.setRefreshing(false);
                            //Log.e("UfoBeaconActivity", "Scanning updated " + ufodevice.getRssi() + " Date " + sdf.format(ufodevice.getDate()) + " Mac = " + ufodevice.getBtdevice().getAddress());
                            Log.d("UfoBeaconActivity", "Scanning updated " + ufodevice.getEddystoneInstance()+ "Distance="+ufodevice.getDistance());
                            ufoAdapter.addDevice(ufodevice);
                            if (ufoDevicesList != null && !ufoDevicesList.contains(ufodevice)) {
                                ufoDevicesList.add(ufodevice);
                                ufodevice.startRangeMonitoring(new OnRangingListener() {
                                    @Override
                                    public void isDeviceInRange(final RangeType range) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (range == RangeType.IN_RANGE) {
                                                    /*Intent serviceIntent = new Intent(MainActivity.this, ForegroundService.class);
                                                    serviceIntent.putExtra("inputExtra", "Beacon found");
                                                    ContextCompat.startForegroundService(MainActivity.this, serviceIntent);*/
                                                   // if(ufodevice.getEddystoneInstance()!= Nullable) {
                                                    //Toast.makeText(context, "Testing.......", Toast.LENGTH_SHORT).show();
                                                    if(ufodevice.getEddystoneInstance() != null && !ufodevice.getEddystoneInstance().isEmpty()){
                                                        String Beaconname=ufodevice.getEddystoneInstance().substring(9);

                                                        RealmResults<Beaconspots_realm> results = realm.where(Beaconspots_realm.class).equalTo("Beaconid",Beaconname).findAll();
                                                        int counterresult=results.size();
                                                        //Toast.makeText(context, ""+counterresult, Toast.LENGTH_SHORT).show();
                                                        if(counterresult>0){
                                                            String getbeacon_id=results.get(0).getBeaconid();

                                                           // Toast.makeText(context, "Testing....", Toast.LENGTH_SHORT).show();

                                                            //Toast.makeText(context, "Welcome...."+getbeacon_id, Toast.LENGTH_SHORT).show();
                                                        }
                                                       // String getbeacon_id=results.get(0).getBeaconid();

                                                        //Toast.makeText(context, "dfssf "+counterresult, Toast.LENGTH_SHORT).show();
                                                        /*String Beaconlastid=getbeacon_id;
                                                        String Beaconname="000000000"+Beaconlastid;
                                                        if(Beaconname.equals(ufodevice.getEddystoneInstance())){


                                                            Toast.makeText(context, "Id found "+Beaconname, Toast.LENGTH_SHORT).show();
                                                        }*/
                                                        //Intent serviceIntent = new Intent(MainActivity.this, ForegroundService.class);
                                                        //serviceIntent.putExtra("inputExtra", "Beacon Found =" + ufodevice.getEddystoneInstance());
                                                        //ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
                                                        //Toast.makeText(context, "Test this", Toast.LENGTH_SHORT).show();
                                                    }
                                                    //instantiate the popup.xml layout file
                                                    //Log.d("Rangedata",""+range);

                                                    //   generateNotification(range, ufodevice);
                                                    //Toast.makeText(context, "Get Beacon", Toast.LENGTH_SHORT).show();
                                                    //MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.wel);
                                                    //ring.start();
                                                    /*ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                                                        // @Override


                                                    });*/
                                                   /* LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                    View customView = layoutInflater.inflate(R.layout.popup,null);
                                                    closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
                                                    //instantiate popup window
                                                    int width = 800;
                                                    int height = 1500;
                                                    boolean focusable = true; // lets taps outside the popup also dismiss it
                                                    final PopupWindow popupWindow = new PopupWindow(customView, width, height, focusable);





                                                    //display the popup window
                                                    popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);*/
                                                  //  Toast.makeText(context, ""+getPackageName(), Toast.LENGTH_SHORT).show();


                                                    //Somewhere set the video name variable
                                                  // getWindow().setFormat(PixelFormat.TRANSLUCENT);
                                                    /*VideoView videoView = findViewById(R.id.videoview);
                                                    final MediaController mediacontroller = new MediaController(MainActivity.this);
                                                    mediacontroller.setAnchorView(videoView);
                                                    videoView.setMediaController(mediacontroller);
                                                    //Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.harsiddhi1); //do not add any extension
                                                    Uri video =   Uri.parse("android.resource://" + getPackageName() +"/" + R.raw.harsiddhi1);
                                                    videoView.setVideoURI(video);
                                                    videoView.requestFocus();
                                                    videoView.start();*/
                                                  /* VideoView videoHolder = new VideoView(MainActivity.this);
//if you want the controls to appear
                                                    //VideoView videoHolder =(VideoView)findViewById(R.id.videoview);
                                                    videoHolder.setMediaController(new MediaController(MainActivity.this));
                                                    Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                                                            + R.raw.harsiddhi1); //do not add any extension
//if your file is named sherif.mp4 and placed in /raw
//use R.raw.sherif
                                                    videoHolder.setVideoURI(video);
                                                    setContentView(videoHolder);
                                                    videoHolder.start();*/


                                                    /*String uri = "android.resource://" + getPackageName() + "/" + R.raw.harsiddhi;
                                                    //Toast.makeText(context, ""+uri, Toast.LENGTH_SHORT).show();
                                                    VideoView mVideoView  = (VideoView)findViewById(R.id.videoView);
                                                    if (mVideoView != null)
                                                    {  mVideoView.setVideoURI(Uri.parse(uri));
                                                        mVideoView.requestFocus();
                                                        mVideoView.start();
                                                    } else
                                                    { //toast or print "mVideoView is null"
                                                        Toast.makeText(context, "Not found.", Toast.LENGTH_SHORT).show();
                                                    }*/
                                                    //close the popup window on button click
                                                 /*   closePopupBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            popupWindow.dismiss();
                                                        }
                                                    });*/
                                                    //String urlpath=R.raw.wel;
                                                    //audioPlayer(R.raw.wel);
                                                    //instantiate popup window
                                                    //popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                                                    //display the popup window
                                                    //popupWindow.showAtLocation(linearLayout1, Gravity.CENTER, 0, 0);
                                                } else {
                                                     generateNotification(range, ufodevice);
                                                }
                                            }
                                        });

                                    }
                                });
                            }

                        }
                    });

                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(final int code, final String message) {
                    // Log.e("startScan", "Error code:- " + code + " Message:- " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            generateToast(code, message);
                        }
                    });


                }
            });
        }

    }


    private void generateNotification(RangeType inRange, UFODevice ufoDevice) {
        // Prepare a notification with vibration, sound and lights
        String msg = ufoDevice.getDeviceType() == UFODeviceType.EDDYSTONE ? "Eddystone" : "iBeacon";
        String range = inRange == RangeType.IN_RANGE ? "in Range" : "out Range";
        String message = "Your " + msg + "(" + ufoDevice.getBtdevice().getAddress() + ") is " + range;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("UFO Beacons")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        // Get an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Build the notification and display it
        mNotifyMgr.notify(ufoDevice.getModelId(), mBuilder.build());
    }

    // Stop the scanning of BLE device
    private void stopScan() {

        if (ufoBeaconManager != null) {
            // swipeContainer.setRefreshing(false);
            tvStopScan.setVisibility(View.GONE);


            ufoBeaconManager.stopScan(new OnSuccessListener() {

                @Override
                public void onSuccess(boolean isStop) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             //generateSuccessToast("Scanning stop");
                        }
                    });


                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(final int code, final String message) {
                    //Log.e("stopScan", "Error code:- " + code + " Message:- " + message);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            generateToast(code, message);
                        }
                    });

                }
            });

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ufoBeaconManager != null) {
            stopScan();
        }
        stopSortingTimer();
    }
    private void stopSortingTimer() {
        if (sortingTimerHandler != null) {
            sortingTimerHandler.removeCallbacksAndMessages(null);
            sortingTimerHandler = null;
        }

        if (sortingTimerHandlerThread != null) {
            sortingTimerHandlerThread.quit();
            sortingTimerHandlerThread = null;
        }
    }
    private void listneScreen() {
//        relMenu.setOnClickListener((View.OnClickListener) this);
        tvStopScan.setOnClickListener(this);
        rel_StartScan.setOnClickListener(this);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                verifyBluetoothState();
            }
        });
    }


    private void openOptionsMenuWithItem() {
        PopupMenu popup = new PopupMenu(MainActivity.this, relMenu);
        // Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_action,
                popup.getMenu());

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class
                            .forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);

                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NewApi")
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_clearList) {
                    if (ufoAdapter != null)
                        ufoAdapter.clearDevices();
                    stopScan();
                    scanButtonVisible();
                    startScan();
                } else if (item.getItemId() == R.id.action_AboutUS) {

                }
                return true;
            }

        });
        popup.show();// showing popup menu
    }

    @Override
    public void onClick(View v) {

        if (v == tvStopScan) {
            swipeContainer.setRefreshing(false);
            stopScan();
            //scanButtonVisible();
        }
        if (v == relMenu) {
            openOptionsMenuWithItem();
        }
        if (v == rel_StartScan) {
            verifyBluetoothState();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    startScan();
                } else {
                    generateSuccessToast("Please enable bluetooth from settings.");
                }
                break;


            default:
                break;
        }
    }

    private void verifyBluetoothState() {
        ufoBeaconManager.isBluetoothEnabled(new OnSuccessListener() {
            @Override
            public void onSuccess(boolean isSuccess) {
                // start scanning
                if (ufoAdapter != null)
                    ufoAdapter.clearDevices();
                stopScan();
                startScan();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(int code, String message) {
                // ask to turn on bluetooth
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        });
        swipeContainer.setRefreshing(false);
    }

    private Handler.Callback sortingTimeoutCallBack = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ufoAdapter != null) {
                        ufoAdapter.updateSortList();
                    }
                }
            });
            startSortingTimer();
            return false;
        }
    };
    private void startSortingTimer() {

        if (sortingTimerHandlerThread == null) {
            sortingTimerHandlerThread = new HandlerThread("sortingTimerHandlerThread");
            sortingTimerHandlerThread.start();
        }
        if (sortingTimerHandler == null)
            sortingTimerHandler = new Handler(sortingTimerHandlerThread.getLooper(), sortingTimeoutCallBack);
        sortingTimerHandler.sendEmptyMessageDelayed(0, 5000);
    }




    public void restartScan() {
        stopScan();
        reIntiateScan();
    }

    private void reIntiateScan() {
        if (ufoBeaconManager != null) {
            ufoBeaconManager.startScan(new OnScanSuccessListener() {
                @Override
                public void onSuccess(final UFODevice ufodevice) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.e("UfoBeaconActivity", "Scanning updated " + ufodevice.getRssi() + " Date " + sdf.format(ufodevice.getDate()) + " Mac = " + ufodevice.getBtdevice().getAddress());
                            if (ufodevice.getBtdevice().getAddress().equalsIgnoreCase(SharedUFODevice.INSTANCE.getUfodevice().getBtdevice().getAddress())) {
                                SharedUFODevice.INSTANCE.setUfodevice(ufodevice);
                            }
                        }
                    });

                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(final int code, final String message) {
                    // Log.e("startScan", "Error code:- " + code + " Message:- " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });


                }
            });
        }
    }
}
