package com.beac.beaconintegartion;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Groupattr extends RealmObject {
    @PrimaryKey
    private int id;
    private  int groupid;
    private  String friendname;
    private  String friendmobile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getFriendname() {
        return friendname;
    }

    public void setFriendname(String friendname) {
        this.friendname = friendname;
    }

    public String getFriendmobile() {
        return friendmobile;
    }

    public void setFriendmobile(String friendmobile) {
        this.friendmobile = friendmobile;
    }
}
