package com.beac.beaconintegartion;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Spotattributes extends RealmObject {
    @PrimaryKey
    private int id;
    private int spotid;
    private String image64_path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpotid() {
        return spotid;
    }

    public void setSpotid(int spotid) {
        this.spotid = spotid;
    }

    public String getImage64_path() {
        return image64_path;
    }

    public void setImage64_path(String image64_path) {
        this.image64_path = image64_path;
    }
}
