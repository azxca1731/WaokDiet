package org.androidtown.dietapp.DTO;

import android.support.annotation.NonNull;

/**
 * Created by latitude7275 on 2017-09-14.
 */

public class FriendItem implements Comparable<FriendItem>{
    private String uid;
    private String name;

    public FriendItem() {
    }

    public FriendItem(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull FriendItem o) {
        return 0;
    }

    //getter setter start
    public String getUid()
    {
        return uid;
    }
    public FriendItem setUid(String uid)
    {
        this.uid=uid; return this;
    }

    public String getName() {
        return name;
    }
    public FriendItem setName(String name) {
        this.name = name;return this;
    }


}
