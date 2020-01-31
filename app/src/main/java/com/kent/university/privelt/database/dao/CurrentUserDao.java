package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.CurrentUser;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CurrentUserDao {
    @Query("SELECT * FROM current_user WHERE id = 0")
    LiveData<CurrentUser> getCurrentUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrentUser(CurrentUser... user);

    @Query("DELETE FROM current_user")
    void deleteCurrentUser();
}
