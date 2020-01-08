package com.kent.university.privelt.database.dao;

import com.kent.university.privelt.model.Credentials;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CredentialsDao {
    @Query("SELECT * FROM credentials")
    LiveData<List<Credentials>> getCredentials();

    @Query("SELECT * FROM credentials WHERE id = :id")
    Credentials getCredentialsWithId(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCredentials(Credentials... credentials);

    @Query("DELETE FROM credentials")
    void deleteAllCredentials();
}
