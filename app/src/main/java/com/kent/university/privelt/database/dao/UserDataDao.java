package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.UserData;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface UserDataDao {
    @Query("SELECT * FROM user_data")
    LiveData<List<UserData>> getUserDatas();

    @Query("DELETE FROM user_data")
    void deleteAllUserDatas();
}
