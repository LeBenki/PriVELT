package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.CurrentUser;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CurrentUserDao {
    @Query("SELECT * FROM current_user WHERE id = 0")
    LiveData<CurrentUser> getCurrentUser();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrentUser(CurrentUser... user);
}
