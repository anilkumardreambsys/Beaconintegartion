package com.beac.beaconintegartion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import com.ufobeaconsdk.main.EddystoneType;
import com.ufobeaconsdk.main.UFODevice;
import com.ufobeaconsdk.main.UFODeviceType;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class UfoBeaconAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private final ArrayList<UFODevice> mListValues = new ArrayList<UFODevice>();
    private Context context;
    private ListView list;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    MediaPlayer mp = new MediaPlayer();



    public UfoBeaconAdapter(Context context, ListView deviceList) {
        this.context = context;
        list = deviceList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Toast.makeText(context, "adapter", Toast.LENGTH_SHORT).show();

    }


    public void addDevice(UFODevice device) {

        if (mListValues != null) {

            if (mListValues.contains(device)) {
                updateDevice(device);

            } else {
                mListValues.add(device);
                notifyDataSetChanged();
            }
        }
    }


    private void updateDevice(UFODevice device) {
        final int indexInBonded = mListValues.indexOf(device);
        final UFODevice ufodevice = mListValues.get(indexInBonded);

//        ufodevice.setRssi(device.getRssi());
//        ufodevice.setDate(device.getDate());

        View view = null;
        view = list.getChildAt(indexInBonded - list.getFirstVisiblePosition());
        if (view != null) {
            TextView rssi = (TextView) view.findViewById(R.id.tvRssi);
            TextView updatedDate = (TextView) view.findViewById(R.id.tvlastUpdated);
            TextView tvRowdata = (TextView) view.findViewById(R.id.tvrawData);
            TextView txPower = (TextView) view.findViewById(R.id.tvTx);
            TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            tvRowdata.setText(ufodevice.getScanRecord());
            rssi.setText(ufodevice.getRssi() + " dBm");
            txPower.setText(ufodevice.getRssiAt1meter() + " dBm");
            tvDistance.setText(String.format("" + ufodevice.getDistance()));
            //Log.d("Distance",""+ufodevice.get);
            //Toast.makeText(context, "Get Beacon", Toast.LENGTH_SHORT).show();

            RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
            Realm.setDefaultConfiguration(configuration);
            Realm.getInstance(configuration);
            Realm realm;
            realm = Realm.getDefaultInstance();
            //ufodevice.getEddystoneInstance();
           // Date currentTime = Calendar.getInstance().getTime();
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            //Toast.makeText(context, "" + currentDate, Toast.LENGTH_SHORT).show();
            Boolean isopen;
            if(ufodevice.getEddystoneInstance() != null && !ufodevice.getEddystoneInstance().isEmpty()){
                final String Beaconname=ufodevice.getEddystoneInstance().substring(9);
                RealmResults<Beaconspots_realm> results = realm.where(Beaconspots_realm.class).equalTo("Beaconid", Beaconname).findAll();
                int counterresult = results.size();
                // Toast.makeText(context, ""+counterresult, Toast.LENGTH_SHORT).show();
                if (counterresult > 0) {

                    String filename = "android.resource://" + context.getPackageName() + "/"+R.raw.wel;



                    SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(context);
                    String checkval = preferences1.getString(Beaconname, "");
                    Boolean isbeacon = preferences1.getBoolean("check_"+Beaconname,true);


                    String toyBornTime = checkval;
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");

                    try {

                        Date oldDate = dateFormat.parse(toyBornTime);
                        //System.out.println(oldDate);

                        Date currentDate1 = new Date();
                        //Toast.makeText(context, ""+currentDate1.getTime(), Toast.LENGTH_SHORT).show();

                        long diff = currentDate1.getTime() - oldDate.getTime();
                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;

                        if (oldDate.before(currentDate1)) {

                            //Log.e("oldDate", "is previous date");
                            Log.d("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                                    + " hours: " + hours + " days: " + days);
                            if(minutes>=1){
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(Beaconname, currentDate);
                                editor.putBoolean("check_" + Beaconname, true);
                                editor.apply();
                            }


                        }

                        // Log.e("toyBornTime", "" + toyBornTime);

                    } catch (ParseException e) {

                        e.printStackTrace();
                    }

                    //Toast.makeText(context, "dfd "+checkval, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "" + checkval, Toast.LENGTH_SHORT).show();
                    int spotid = results.get(0).getId();
                    String getbeacon_id = results.get(0).getBeaconid();
                    String getSpot_name = results.get(0).getSpot_name();
                    String get_beaconid = results.get(0).getBeaconid();
                    String get_lat = results.get(0).getLatitude();
                    String get_long = results.get(0).getLongitude();
                    String get_audiopath = results.get(0).getAudio_path();
                    View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);

                    String uri = "android.resource://com.beac.beaconintegartion/" + R.raw.harsiddhi;
                    //Toast.makeText(context, ""+uri, Toast.LENGTH_SHORT).show();
                    VideoView mVideoView = (VideoView) alertLayout.findViewById(R.id.videoview);

                    if (mVideoView != null) {
                        mVideoView.setVideoURI(Uri.parse(uri));
                        // mVideoView.requestFocus();
                        mVideoView.start();
                    } else { //toast or print "mVideoView is null"
                        //Toast.makeText(context, "Not found.", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Spotid",""+spotid);
                    RealmResults<Spotattributes> spotresults = realm.where(Spotattributes.class).equalTo("spotid", spotid).findAll();
                    for (int i = 0; i < spotresults.size(); i++) {
                        int spotarr_id = spotresults.get(i).getSpotid();
                        String getSpotarr_name = spotresults.get(i).getImage64_path();
                        //Log.d("spotid",""+getSpotarr_name);
                        LinearLayout layout = alertLayout.findViewById(R.id.img_gallery);
                        ImageView image = new ImageView(context);
                        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        byte[] decodedString = Base64.decode(getSpotarr_name, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        image.setImageBitmap(decodedByte);
                        layout.addView(image);

                    }


                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle(getSpot_name);
                    // this is set the view from XML inside AlertDialog
                    alert.setView(alertLayout);
                    // disallow cancel of AlertDialog on click of back button and outside touch
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("check_" + Beaconname, false);
                            editor.apply();
                            //dialog.show();
                        }
                    });
                    AlertDialog dialog = alert.create();
                    //dialog.show();
                    if(isbeacon) {
                            //if its visibility is not showing then show here
                            //Toast.makeText(context, "hello123 ", Toast.LENGTH_SHORT).show();

                            dialog.show();
                        mp.reset();
                        try {
                            mp.setDataSource(context,Uri.parse(filename));
                            mp.prepare();
                            mp.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Beaconname, currentDate);
                            editor.putBoolean("check_" + Beaconname, false);
                            editor.apply();
                    } else {

                       // Toast.makeText(context, "test ", Toast.LENGTH_SHORT).show();
                        //do something here... if already showing
                        //dialog.dismiss();
                    }

                }
            }
            /*AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Write your message here.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();*/

            updatedDate.setText(sdf.format(ufodevice.getDate()));
            if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.EDDYSTONE) {
                TextView tviBeacon = (TextView) view.findViewById(R.id.tvibeacon);
                TextView tvEddystoneUIDTitle = (TextView) view.findViewById(R.id.tvEddystoneId);
                TextView tvEddystoneURLTitle = (TextView) view.findViewById(R.id.tvEddystoneUrl);
                TextView tvEddystoneTLMTitle = (TextView) view.findViewById(R.id.tvEddystoneTLM);
                TextView tvNamespaceId = (TextView) view.findViewById(R.id.tvEddystone_UID_namespaceId);
                TextView tvInstanceId = (TextView) view.findViewById(R.id.tvEddystone_UID_instaceId);
                TextView tvURL = (TextView) view.findViewById(R.id.tvEddystone_URL_url);
                TextView tvBatteryVoltage = (TextView) view.findViewById(R.id.tvEddystone_TLM_BatteyVoltage);
                TextView tvTemperature = (TextView) view.findViewById(R.id.tvEddystone_TLM_Temperature);
                TextView tvBootTime = (TextView) view.findViewById(R.id.tvEddystone_TLM_boottime);
                TextView tvPduCount = (TextView) view.findViewById(R.id.tvEddystone_TLM_pducount);
                LinearLayout lnrEddystone = (LinearLayout) view.findViewById(R.id.lnr_ufoeddystone_main);
                LinearLayout lnrBeacon = (LinearLayout) view.findViewById(R.id.lnr_ufobeacon_main);
                LinearLayout lnrEddystoneUID = (LinearLayout) view.findViewById(R.id.linear_eddystone_uid);
                LinearLayout lnrEddystoneURL = (LinearLayout) view.findViewById(R.id.linear_eddystone_url);
                LinearLayout lnrEddystoneTLM = (LinearLayout) view.findViewById(R.id.linear_eddystone_tlm);

                lnrEddystone.setVisibility(View.VISIBLE);
                lnrBeacon.setVisibility(View.GONE);
                if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID_URL_TLM) {
                    lnrEddystoneUID.setVisibility(View.VISIBLE);
                    tvEddystoneUIDTitle.setVisibility(View.VISIBLE);
                    tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                    tviBeacon.setText(context.getString(R.string.eddystoneuid) + " + URL + TLM");
                    lnrEddystoneURL.setVisibility(View.VISIBLE);
                    tvEddystoneURLTitle.setVisibility(View.VISIBLE);
                    tvURL.setText(ufodevice.getEddystoneURL() + "");
                    lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    tvEddystoneTLMTitle.setVisibility(View.VISIBLE);
                    tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                }
//               /* else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID && ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_URL) {
//                    tviBeacon.setText(context.getString(R.string.eddystoneuid) + " + URL");
//                    lnrEddystoneUID.setVisibility(View.VISIBLE);
//                    tvEddystoneUIDTitle.setVisibility(View.VISIBLE);
//                    tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
//                    tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
//                    lnrEddystoneURL.setVisibility(View.VISIBLE);
//                    tvEddystoneURLTitle.setVisibility(View.VISIBLE);
//                    tvURL.setText(ufodevice.getEddystoneURL() + "");
//                    lnrEddystoneTLM.setVisibility(View.GONE);
//                } */
                else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID_TLM) {
                    tviBeacon.setText(context.getString(R.string.eddystoneuid) + " + TLM");
                    lnrEddystoneUID.setVisibility(View.VISIBLE);
                    tvEddystoneUIDTitle.setVisibility(View.VISIBLE);
                    tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                    lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    tvEddystoneTLMTitle.setVisibility(View.VISIBLE);
                    tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                    lnrEddystoneURL.setVisibility(View.GONE);
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_URL_TLM) {
                    tviBeacon.setText(context.getString(R.string.eddystoneurl) + " + TLM");
                    lnrEddystoneURL.setVisibility(View.VISIBLE);
                    tvEddystoneURLTitle.setVisibility(View.VISIBLE);
                    tvURL.setText(ufodevice.getEddystoneURL() + "");
                    lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    tvEddystoneTLMTitle.setVisibility(View.VISIBLE);
                    tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                    lnrEddystoneUID.setVisibility(View.GONE);
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID) {
                    tviBeacon.setText(context.getString(R.string.eddystoneuid));
                    tvEddystoneUIDTitle.setVisibility(View.GONE);
                    lnrEddystoneTLM.setVisibility(View.GONE);
                    lnrEddystoneURL.setVisibility(View.GONE);
                    lnrEddystoneUID.setVisibility(View.VISIBLE);
                    tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_URL) {
                    tviBeacon.setText(context.getString(R.string.eddystoneurl));
                    tvEddystoneURLTitle.setVisibility(View.GONE);
                    lnrEddystoneTLM.setVisibility(View.GONE);
                    lnrEddystoneUID.setVisibility(View.GONE);
                    lnrEddystoneURL.setVisibility(View.VISIBLE);
                    tvURL.setText(ufodevice.getEddystoneURL() + "");
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_TLM) {
                    tviBeacon.setText(context.getString(R.string.eddystonetlm));
                    tvEddystoneTLMTitle.setVisibility(View.GONE);
                    lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    lnrEddystoneUID.setVisibility(View.GONE);
                    lnrEddystoneURL.setVisibility(View.GONE);
                    tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                }
            }
        }
    }

    public void updateSortList() {
        sortListByRSSI();
        notifyDataSetChanged();
    }

    public void clearDevices() {
        if (mListValues != null && mListValues.size() > 0) {
            mListValues.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mListValues.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.item_ufobeacon, null);
            holder = new ViewHolder();

            holder.tvUnknown = (TextView) view.findViewById(R.id.tvunknown);
            holder.tviBeacon = (TextView) view.findViewById(R.id.tvibeacon);
            holder.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            holder.tvMacid = (TextView) view.findViewById(R.id.tvmacId);
            holder.tvMajor = (TextView) view.findViewById(R.id.tvmajor);
            holder.tvMinor = (TextView) view.findViewById(R.id.tvminor);
            holder.tvTxpower = (TextView) view.findViewById(R.id.tvTx);
            holder.tvRssi = (TextView) view.findViewById(R.id.tvRssi);
            holder.tvUUID = (TextView) view.findViewById(R.id.tvUUID);
            holder.tvLastUpdate = (TextView) view.findViewById(R.id.tvlastUpdated);
            holder.tvRowdata = (TextView) view.findViewById(R.id.tvrawData);
            holder.tvEddystoneUIDTitle = (TextView) view.findViewById(R.id.tvEddystoneId);
            holder.tvEddystoneURLTitle = (TextView) view.findViewById(R.id.tvEddystoneUrl);
            holder.tvEddystoneTLMTitle = (TextView) view.findViewById(R.id.tvEddystoneTLM);
            holder.tvNamespaceId = (TextView) view.findViewById(R.id.tvEddystone_UID_namespaceId);
            holder.tvInstanceId = (TextView) view.findViewById(R.id.tvEddystone_UID_instaceId);
            holder.tvURL = (TextView) view.findViewById(R.id.tvEddystone_URL_url);
            holder.tvBatteryVoltage = (TextView) view.findViewById(R.id.tvEddystone_TLM_BatteyVoltage);
            holder.tvTemperature = (TextView) view.findViewById(R.id.tvEddystone_TLM_Temperature);
            holder.tvBootTime = (TextView) view.findViewById(R.id.tvEddystone_TLM_boottime);
            holder.tvPduCount = (TextView) view.findViewById(R.id.tvEddystone_TLM_pducount);
            holder.lnrEddystone = (LinearLayout) view.findViewById(R.id.lnr_ufoeddystone_main);
            holder.lnrBeacon = (LinearLayout) view.findViewById(R.id.lnr_ufobeacon_main);
            holder.lnrEddystoneUID = (LinearLayout) view.findViewById(R.id.linear_eddystone_uid);
            holder.lnrEddystoneURL = (LinearLayout) view.findViewById(R.id.linear_eddystone_url);
            holder.lnrEddystoneTLM = (LinearLayout) view.findViewById(R.id.linear_eddystone_tlm);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (mListValues != null && mListValues.size() > 0) {

            final UFODevice ufodevice = mListValues
                    .get(position);
            final String name = ufodevice.getBtdevice().getName();
            holder.tvUnknown.setText(name != null ? name : "N/A");
            holder.tvMacid.setText(ufodevice.getBtdevice().getAddress());
            holder.tvRssi.setText(ufodevice.getRssi() + " dBm");
            holder.tvTxpower.setText(ufodevice.getRssiAt1meter() + " dBM");
            holder.tvDistance.setText(ufodevice.getDistanceInString());

            if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.IBEACON) {
                holder.lnrEddystone.setVisibility(View.GONE);
                holder.lnrBeacon.setVisibility(View.VISIBLE);
                holder.tviBeacon.setText("iBeacon");
                holder.tvMajor.setText(ufodevice.getMajor() + "");
                holder.tvMinor.setText(ufodevice.getMinor() + "");
                holder.tvUUID.setText(ufodevice.getProximityUUID() + "");
                holder.tvRowdata.setText(ufodevice.getScanRecord());
                holder.tvLastUpdate.setText(sdf.format(ufodevice.getDate()));
            } else if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.EDDYSTONE) {
                holder.lnrEddystone.setVisibility(View.VISIBLE);
                holder.lnrBeacon.setVisibility(View.GONE);
                if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID_URL_TLM) {
                    holder.lnrEddystoneUID.setVisibility(View.VISIBLE);
                    holder.tvEddystoneUIDTitle.setVisibility(View.VISIBLE);
                    holder.tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    holder.tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                    holder.tviBeacon.setText(context.getString(R.string.eddystoneuid) + " + URL + TLM");
                    holder.lnrEddystoneURL.setVisibility(View.VISIBLE);
                    holder.tvEddystoneURLTitle.setVisibility(View.VISIBLE);
                    holder.tvURL.setText(ufodevice.getEddystoneURL() + "");
                    holder.lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    holder.tvEddystoneTLMTitle.setVisibility(View.VISIBLE);
                    holder.tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    holder.tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    holder.tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    holder.tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                }
               /* else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID_R && ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_URL) {
                    holder.tviBeacon.setText(context.getString(R.string.eddystoneuid) + " + URL");
                    holder.lnrEddystoneUID.setVisibility(View.VISIBLE);
                    holder.tvEddystoneUIDTitle.setVisibility(View.VISIBLE);
                    holder.tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    holder.tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                    holder.lnrEddystoneURL.setVisibility(View.VISIBLE);
                    holder.tvEddystoneURLTitle.setVisibility(View.VISIBLE);
                    holder.tvURL.setText(ufodevice.getEddystoneURL() + "");
                    holder.lnrEddystoneTLM.setVisibility(View.GONE);
                }*/
                else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID_TLM) {
                    holder.tviBeacon.setText(context.getString(R.string.eddystoneuid) + " + TLM");
                    holder.lnrEddystoneUID.setVisibility(View.VISIBLE);
                    holder.tvEddystoneUIDTitle.setVisibility(View.VISIBLE);
                    holder.tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    holder.tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                    holder.lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    holder.tvEddystoneTLMTitle.setVisibility(View.VISIBLE);
                    holder.tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    holder.tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    holder.tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    holder.tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                    holder.lnrEddystoneURL.setVisibility(View.GONE);
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_URL_TLM) {
                    holder.tviBeacon.setText(context.getString(R.string.eddystoneurl) + " + TLM");
                    holder.lnrEddystoneURL.setVisibility(View.VISIBLE);
                    holder.tvEddystoneURLTitle.setVisibility(View.VISIBLE);
                    holder.tvURL.setText(ufodevice.getEddystoneURL() + "");
                    holder.lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    holder.tvEddystoneTLMTitle.setVisibility(View.VISIBLE);
                    holder.tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    holder.tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    holder.tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    holder.tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                    holder.lnrEddystoneUID.setVisibility(View.GONE);
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_UID) {
                    holder.tviBeacon.setText(context.getString(R.string.eddystoneuid));
                    holder.tvEddystoneUIDTitle.setVisibility(View.GONE);
                    holder.lnrEddystoneTLM.setVisibility(View.GONE);
                    holder.lnrEddystoneURL.setVisibility(View.GONE);
                    holder.lnrEddystoneUID.setVisibility(View.VISIBLE);
                    holder.tvNamespaceId.setText(ufodevice.getEddystoneNameSpace() + "");
                    holder.tvInstanceId.setText(ufodevice.getEddystoneInstance() + "");
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_URL) {
                    holder.tviBeacon.setText(context.getString(R.string.eddystoneurl));
                    holder.tvEddystoneURLTitle.setVisibility(View.GONE);
                    holder.lnrEddystoneTLM.setVisibility(View.GONE);
                    holder.lnrEddystoneUID.setVisibility(View.GONE);
                    holder.lnrEddystoneURL.setVisibility(View.VISIBLE);
                    holder.tvURL.setText(ufodevice.getEddystoneURL() + "");
                } else if (ufodevice.getEddystoneType() == EddystoneType.EDDYSTONE_TLM) {
                    holder.tviBeacon.setText(context.getString(R.string.eddystonetlm));
                    holder.tvEddystoneTLMTitle.setVisibility(View.GONE);
                    holder.lnrEddystoneTLM.setVisibility(View.VISIBLE);
                    holder.lnrEddystoneUID.setVisibility(View.GONE);
                    holder.lnrEddystoneURL.setVisibility(View.GONE);
                    holder.tvBatteryVoltage.setText(ufodevice.getEddystoneTLMBatteryVoltage() + " V");
                    holder.tvTemperature.setText(ufodevice.getEddystoneTLMTemperature() + context.getString(R.string.celsiusUnicode));
                    holder.tvBootTime.setText(sdf.format(ufodevice.getEddystoneActiveSince()));
                    holder.tvPduCount.setText(ufodevice.getEddystoneTLMPDUCounts() + "");
                }
            }
            holder.tvRowdata.setText(ufodevice.getScanRecord());
            holder.tvLastUpdate.setText(sdf.format(ufodevice.getDate()));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Data Adapter", Toast.LENGTH_SHORT).show();
                /*SharedUFODevice.INSTANCE.setUfodevice(mListValues.get(position));
                ((UFOBeaconActivity)context).restartScan();
                Intent intent = new Intent(context, BeaconDetailActivity.class);
                context.startActivity(intent);*/
            }
        });
        return view;
    }

    public void sortListByRSSI() {
        if (mListValues != null && mListValues.size() > 0)
            Collections.sort(mListValues);
    }

    class ViewHolder {
        TextView tvUnknown, tviBeacon, tvMacid, tvMajor, tvMinor, tvTxpower,
                tvRssi, tvUUID, tvLastUpdate, tvRowdata, tvDistance;
        TextView tvEddystoneUIDTitle, tvEddystoneURLTitle, tvEddystoneTLMTitle;
        TextView tvNamespaceId, tvInstanceId;
        TextView tvURL;
        TextView tvBatteryVoltage, tvTemperature, tvBootTime, tvPduCount;
        LinearLayout lnrEddystone, lnrBeacon;
        LinearLayout lnrEddystoneUID, lnrEddystoneURL, lnrEddystoneTLM;

    }
}
