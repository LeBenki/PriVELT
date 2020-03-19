package com.kent.university.privelt.database;

import android.content.Context;

import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.database.dao.CurrentUserDao;
import com.kent.university.privelt.database.dao.ServiceDao;
import com.kent.university.privelt.database.dao.SettingsDao;
import com.kent.university.privelt.database.dao.UserDataDao;
import com.kent.university.privelt.model.CurrentUser;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.Settings;
import com.kent.university.privelt.model.UserData;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserData.class, Service.class, CurrentUser.class, Settings.class}, version = 4, exportSchema = false)
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
                            .addMigrations(MIGRATION_3_4)
                            .openHelperFactory(factory)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `settings` (`id` INTEGER, `googleDriveAutoSave` INTEGER, `googleDriveFileID` TEXT, PRIMARY KEY(`id`))");
        }
    };

    public static void nullDatabase() {
        synchronized (PriVELTDatabase.class) {
            INSTANCE = null;
        }
    }

    public abstract UserDataDao userDataDao();

    public abstract ServiceDao serviceDao();

    public abstract CurrentUserDao currentUserDao();

    public abstract SettingsDao settingsDao();
}

