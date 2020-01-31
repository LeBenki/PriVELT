package com.kent.university.privelt.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.commonsware.cwac.saferoom.SQLCipherUtils;
import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.database.dao.CredentialsDao;
import com.kent.university.privelt.database.dao.CurrentUserDao;
import com.kent.university.privelt.database.dao.ServiceDao;
import com.kent.university.privelt.database.dao.UserDataDao;
import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.model.CurrentUser;
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

@Database(entities = {Credentials.class, UserData.class, Service.class, CurrentUser.class}, version = 2, exportSchema = false)
public abstract class PriVELTDatabase extends RoomDatabase {
    private static volatile PriVELTDatabase INSTANCE;
    public final static int DB_SIZE = 1024;

    public static void changeMasterPassword() {
        SafeHelperFactory.rekey(INSTANCE.mDatabase, PriVELT.getInstance().getIdentityManager().getPassword());
    }

    public static PriVELTDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PriVELTDatabase.class) {
                if (INSTANCE == null) {
                    SafeHelperFactory factory = SafeHelperFactory.fromUser(PriVELT.getInstance().getIdentityManager().getPassword());

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PriVELTDatabase.class,
                            "PriVELTDatabase.db")
                            .fallbackToDestructiveMigration()
                            .addCallback(prepopulateDatabase())
                            .openHelperFactory(factory)
                            .build();

                    Log.d("LULUCAS", String.valueOf(SQLCipherUtils.getDatabaseState(context, "PriVELTDatabase.db")));
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
                fillDbWithDummyPasswordAndUser(db);
            }
        };
    }

    static private void fillDbWithDummyPasswordAndUser(@NonNull SupportSQLiteDatabase db) {
        for (int i = 0; i < DB_SIZE; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", i);
            contentValues.put("email", "name" + i);
            contentValues.put("password", SimpleHash.getHashedPassword(SimpleHash.HashMethod.SHA256, UUID.randomUUID().toString()));
            db.insert("credentials", OnConflictStrategy.REPLACE, contentValues);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", 0);
        db.insert("current_user", OnConflictStrategy.REPLACE, contentValues);
    }

    public abstract CredentialsDao credentialsDao();

    public abstract UserDataDao userDataDao();

    public abstract ServiceDao serviceDao();

    public abstract CurrentUserDao currentUserDao();
}

