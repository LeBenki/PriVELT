package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.Settings;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 0")
    LiveData<Settings> getSettings();

    @Query("SELECT * FROM settings WHERE id = 0")
    Settings getInstantSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateSettings(Settings... user);

    @Query("DELETE FROM settings")
    void deleteSettings();
}
