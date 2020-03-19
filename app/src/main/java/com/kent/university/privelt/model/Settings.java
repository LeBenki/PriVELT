package com.kent.university.privelt.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {

    @PrimaryKey
    private long id;

    private boolean googleDriveAutoSave;

    private String googleDriveFileID;

    public Settings(boolean googleDriveAutoSave, String googleDriveFileID) {
        this.id = 0;
        this.googleDriveAutoSave = googleDriveAutoSave;
        this.googleDriveFileID = googleDriveFileID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isGoogleDriveAutoSave() {
        return googleDriveAutoSave;
    }

    public void setGoogleDriveAutoSave(boolean googleDriveAutoSave) {
        this.googleDriveAutoSave = googleDriveAutoSave;
    }

    public String getGoogleDriveFileID() {
        return googleDriveFileID;
    }

    public void setGoogleDriveFileID(String googleDriveFileID) {
        this.googleDriveFileID = googleDriveFileID;
    }
}
