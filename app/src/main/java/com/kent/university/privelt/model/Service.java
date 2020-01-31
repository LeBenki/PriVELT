package com.kent.university.privelt.model;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "service")
public class Service implements Serializable {

    public final static String DELIMITER = "@/:-";

    private String name;

    @PrimaryKey(autoGenerate = true)
    private long id;

    private boolean isPasswordSaved;

    private String concatenatedScripts;

    private String user;

    private String password;

    public Service(String name, boolean isPasswordSaved, String concatenatedScripts, String user, String password) {
        this.name = name;
        this.isPasswordSaved = isPasswordSaved;
        this.concatenatedScripts = concatenatedScripts;
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
