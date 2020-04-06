/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.UserData;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDataDao {
    @Query("SELECT * FROM user_data")
    LiveData<List<UserData>> getUserData();

    @Query("DELETE FROM user_data")
    void deleteAllUserData();

    @Query("DELETE FROM user_data WHERE service_id = :serviceId")
    void deleteUserDataForAService(long serviceId);

    @Query("SELECT * FROM user_data WHERE service_id = :serviceId and type =:type")
    LiveData<List<UserData>> getUserDataForAServiceAndType(long serviceId, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserData(UserData... userData);
}
