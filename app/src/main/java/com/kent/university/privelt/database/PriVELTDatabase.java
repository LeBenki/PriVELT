package com.kent.university.privelt.database;

import android.content.ContentValues;
import android.content.Context;

import com.kent.university.privelt.database.dao.CredentialsDao;
import com.kent.university.privelt.database.dao.ServiceDao;
import com.kent.university.privelt.database.dao.UserDataDao;
import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.utils.SimpleHash;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Credentials.class, UserData.class, Service.class}, version = 4, exportSchema = false)
public abstract class PriVELTDatabase extends RoomDatabase {
    private static volatile PriVELTDatabase INSTANCE;
    public final static int DB_SIZE = 1024;

    public static PriVELTDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PriVELTDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PriVELTDatabase.class,
                            "PriVELTDatabase.db")
                            .addCallback(prepopulateDatabase())
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback prepopulateDatabase() {
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                for (int i = 0; i < DB_SIZE; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", i);
                    contentValues.put("email", "name" + i);
                    contentValues.put("password", SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, UUID.randomUUID().toString()));
                    db.insert("credentials", OnConflictStrategy.IGNORE, contentValues);
                }
            }
        };
    }

    public abstract CredentialsDao credentialsDao();

    public abstract UserDataDao userDataDao();

    public abstract ServiceDao serviceDao();
}

