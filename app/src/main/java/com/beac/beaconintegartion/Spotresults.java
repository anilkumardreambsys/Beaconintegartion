package com.beac.beaconintegartion;

public class Spotresults {
    int id;
    String spot_name, Beaconid,latitude,longitude,audio_path,image_path;

    public Spotresults(int id,String spot_name, String Beaconid, String latitude ,String longitude,String audio_path,String image_path) {
        this.spot_name = spot_name;
        this.id = id;
        this.Beaconid = Beaconid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.audio_path = audio_path;
        this.image_path = image_path;
    }

    public int getId() {
        return id;
    }

    public String getSpot_name() {
        return spot_name;
    }

    public String getBeaconid() {
        return Beaconid;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public String getImage_path() {
        return image_path;
    }
}
