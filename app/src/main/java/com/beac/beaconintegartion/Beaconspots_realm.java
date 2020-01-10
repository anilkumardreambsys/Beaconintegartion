package com.beac.beaconintegartion;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Beaconspots_realm extends RealmObject {
    @PrimaryKey
    private int id;
    private  String spot_name;
    private  String Beaconid;
    private  String latitude;
    private  String longitude;
    private  String audio_path;
    private  String image_path;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpot_name() {
        return spot_name;
    }

    public void setSpot_name(String spot_name) {
        this.spot_name = spot_name;
    }

    public String getBeaconid() {
        return Beaconid;
    }

    public void setBeaconid(String beaconid) {
        Beaconid = beaconid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }
}
