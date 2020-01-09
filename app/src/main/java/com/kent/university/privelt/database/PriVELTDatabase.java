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
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Credentials.class, UserData.class, Service.class}, version = 5, exportSchema = false)
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
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            fillDbWithDummyPassword(database);
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            fillDbWithDummyPassword(database);
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            fillDbWithDummyPassword(database);
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            fillDbWithDummyPassword(database);
        }
    };

    private static Callback prepopulateDatabase() {
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                fillDbWithDummyPassword(db);
            }
        };
    }

    static private void fillDbWithDummyPassword(@NonNull SupportSQLiteDatabase db) {
        for (int i = 0; i < DB_SIZE; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", i);
            contentValues.put("email", "name" + i);
            contentValues.put("password", SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, UUID.randomUUID().toString()));
            db.insert("credentials", OnConflictStrategy.REPLACE, contentValues);
        }
    }

    public abstract CredentialsDao credentialsDao();

    public abstract UserDataDao userDataDao();

    public abstract ServiceDao serviceDao();
}

