package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.Service;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ServiceDao {
    @Query("SELECT * FROM service")
    LiveData<List<Service>> getServices();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateServices(Service... service);
}
