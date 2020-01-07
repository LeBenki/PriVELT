package com.kent.university.privelt.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "service",
        foreignKeys = @ForeignKey(entity = Credentials.class,
        parentColumns = "id",
        childColumns = "credentials_id",
        onDelete = CASCADE))
public class Service {

    @ColumnInfo(name = "res_id")
    private int resId;

    private String name;

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "credentials_id", index = true)
    private long credentialsId;

    private boolean subscribed;

    public Service(long id, int resId, String name, long credentialsId, boolean subscribed) {
        this.id = id;
        this.resId = resId;
        this.name = name;
        this.credentialsId = credentialsId;
        this.subscribed = subscribed;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public long getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(long credentialsId) {
        this.credentialsId = credentialsId;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
