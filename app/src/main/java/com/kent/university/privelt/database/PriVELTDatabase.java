package com.kent.university.privelt.database;

import android.content.Context;
import android.util.Log;

import com.commonsware.cwac.saferoom.SQLCipherUtils;
import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.database.dao.CurrentUserDao;
import com.kent.university.privelt.database.dao.ServiceDao;
import com.kent.university.privelt.database.dao.UserDataDao;
import com.kent.university.privelt.model.CurrentUser;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserData.class, Service.class, CurrentUser.class}, version = 3, exportSchema = false)
public abstract class PriVELTDatabase extends RoomDatabase {
    private static volatile PriVELTDatabase INSTANCE;
    public static final String PriVELTDatabaseName = "PriVELTDatabase.db";

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
                            PriVELTDatabaseName)
                            .fallbackToDestructiveMigration()
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void nullDatabase() {
        synchronized (PriVELTDatabase.class) {
            INSTANCE = null;
        }
    }

    public abstract UserDataDao userDataDao();

    public abstract ServiceDao serviceDao();

    public abstract CurrentUserDao currentUserDao();
}

