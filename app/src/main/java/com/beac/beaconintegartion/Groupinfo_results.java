package com.beac.beaconintegartion;

public class Groupinfo_results {
    int id;
    String group_name, group_pwd,group_phone;

    public Groupinfo_results(int id,String group_name, String group_pwd, String group_phone) {
        this.group_name = group_name;
        this.id = id;
        this.group_phone = group_phone;
        this.group_pwd = group_pwd;
    }

    public int getId() {
        return id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_pwd() {
        return group_pwd;
    }

    public String getGroup_phone() {
        return group_phone;
    }
}
