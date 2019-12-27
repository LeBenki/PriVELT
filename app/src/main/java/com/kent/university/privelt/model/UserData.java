package com.kent.university.privelt.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "user_data",
        foreignKeys = @ForeignKey(entity = Credentials.class,
                parentColumns = "id",
                childColumns = "credentials_id",
                onDelete = CASCADE))
public class UserData {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "credentials_id", index = true)
    private long credentialsId;

    private String welcomeText;

    public UserData(long credentialsId, String welcomeText) {
        this.credentialsId = credentialsId;
        this.welcomeText = welcomeText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(long credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }
}
