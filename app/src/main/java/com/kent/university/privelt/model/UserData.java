/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "user_data",
        foreignKeys = @ForeignKey(entity = Service.class,
        parentColumns = "id",
        childColumns = "service_id",
        onDelete = CASCADE))
public class UserData {

    public final static String DELIMITER = "@/:-";

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;

    private String type;

    private String value;

    private String concatenatedData;

    @ColumnInfo(name = "service_id", index = true)
    private long serviceId;

    public UserData(String title, String type, String value, String concatenatedData, long serviceId) {
        this.title = title;
        this.type = type;
        this.value = value;
        this.concatenatedData = concatenatedData;
        this.serviceId = serviceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConcatenatedData() {
        return concatenatedData;
    }

    public String[] getUnConcatenatedData() {
        return concatenatedData.split(DELIMITER);
    }

    public void setConcatenatedData(String concatenatedData) {
        this.concatenatedData = concatenatedData;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }
}
