package com.kent.university.privelt.model;

import com.kent.university.privelt.utils.SimpleHash;

import java.io.Serializable;

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
public class Service implements Serializable {

    public final static String DELIMITER = "@/:-";

    private String name;

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "credentials_id", index = true)
    private long credentialsId;

    private boolean isPasswordSaved;

    private String concatenatedScripts;

    public Service(String name, boolean isPasswordSaved, String concatenatedScripts) {
        this.name = name;
        this.credentialsId = SimpleHash.calculateIndexOfHash(name);
        this.isPasswordSaved = isPasswordSaved;
        this.concatenatedScripts = concatenatedScripts;
    }

    public boolean isPasswordSaved() {
        return isPasswordSaved;
    }

    public void setPasswordSaved(boolean passwordSaved) {
        isPasswordSaved = passwordSaved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setConcatenatedScripts(String concatenatedScripts) {
        this.concatenatedScripts = concatenatedScripts;
    }

    public String[] getUnConcatenatedScripts() {
        return concatenatedScripts.split(DELIMITER);
    }

    public String getConcatenatedScripts() {
        return concatenatedScripts;
    }

    public long getCredentialsId() {
        return credentialsId;
    }

    public void setCredentialsId(long credentialsId) {
        this.credentialsId = credentialsId;
    }
}
