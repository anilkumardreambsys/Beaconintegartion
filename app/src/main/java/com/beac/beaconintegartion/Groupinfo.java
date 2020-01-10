package com.beac.beaconintegartion;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Groupinfo extends RealmObject {
    @PrimaryKey
    private int id;
    private  String group_name;
    private  String grp_password;
    private  String grp_phone;

    public String getGrp_phone() {
        return grp_phone;
    }

    public void setGrp_phone(String grp_phone) {
        this.grp_phone = grp_phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGrp_password() {
        return grp_password;
    }

    public void setGrp_password(String grp_password) {
        this.grp_password = grp_password;
    }
}
