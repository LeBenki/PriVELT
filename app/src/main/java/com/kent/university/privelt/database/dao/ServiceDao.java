/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.Service;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ServiceDao {
    @Query("SELECT * FROM service")
    LiveData<List<Service>> getServices();

    @Query("SELECT * FROM service")
    List<Service> getAllServices();

    @Insert
    void insertServices(Service... service);

    @Delete
    void deleteServices(Service... service);

    @Update
    void updateServices(Service... services);

    @Query("DELETE FROM service")
    void deleteAllServices();
}
