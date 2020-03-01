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
    LiveData<List<UserData>> getUserDatas();

    @Query("DELETE FROM user_data")
    void deleteAllUserDatas();

    @Query("DELETE FROM user_data WHERE service_id = :serviceId")
    void deleteUserDatasForAService(long serviceId);

    @Query("SELECT * FROM user_data WHERE service_id = :serviceId and type =:type")
    LiveData<List<UserData>> getUserDatasForAServiceAndType(long serviceId, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserDatas(UserData... userData);
}
