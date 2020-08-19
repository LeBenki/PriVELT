/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.kent.university.privelt.PriVELTApplication.Companion.instance
import com.kent.university.privelt.database.dao.*
import com.kent.university.privelt.model.*

@Database(entities = [UserData::class, Service::class, CurrentUser::class, Settings::class, HistoryPermission::class], version = 11, exportSchema = false)
abstract class PriVELTDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao?
    abstract fun serviceDao(): ServiceDao?
    abstract fun currentUserDao(): CurrentUserDao?
    abstract fun settingsDao(): SettingsDao?
    abstract fun historyPermissionDao(): HistoryPermissionDao?

    companion object {
        @Volatile
        private var INSTANCE: PriVELTDatabase? = null
        const val PriVELTDatabaseName = "PriVELTDatabase.db"
        fun changeMasterPassword() {
            SafeHelperFactory.rekey(INSTANCE!!.mDatabase, instance!!.identityManager!!.password)
        }

        fun getInstance(context: Context): PriVELTDatabase? {
            if (INSTANCE == null) {
                synchronized(PriVELTDatabase::class.java) {
                    if (INSTANCE == null) {
                        val factory = SafeHelperFactory.fromUser(instance!!.identityManager!!.password)
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                PriVELTDatabase::class.java,
                                PriVELTDatabaseName)
                                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
                                .openHelperFactory(factory)
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `settings` (`id` INTEGER, `googleDriveAutoSave` INTEGER, `googleDriveFileID` TEXT, PRIMARY KEY(`id`))")
            }
        }

        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `sensor_status` (`id` INTEGER, `sensorName` TEXT, `date` INTEGER, `wereActivated` INTEGER , PRIMARY KEY(`id`))")
            }
        }

        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `user_data` ADD COLUMN `date` INTEGER default 0 NOT NULL")
            }
        }

        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `permission_status` (`id` INTEGER NOT NULL, `permissionName` TEXT NOT NULL, `date` INTEGER NOT NULL, `wereActivated` INTEGER NOT NULL, `applicationPackage` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        private val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `history_permission` (`id` INTEGER NOT NULL, `date` INTEGER NOT NULL, `locationSensor` INTEGER NOT NULL,`bluetoothSensor` INTEGER NOT NULL, `nfcSensor` INTEGER NOT NULL, `wifiSensor` INTEGER NOT NULL,`locationValue` INTEGER NOT NULL, `contactsValue` INTEGER NOT NULL, `bluetoothValue` INTEGER NOT NULL,`storageValue` INTEGER NOT NULL,`wifiValue` INTEGER NOT NULL,`nfcValue` INTEGER NOT NULL,`calendarValue` INTEGER NOT NULL,`smsValue` INTEGER NOT NULL,PRIMARY KEY(`id`))")
            }
        }

        private val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `microValue` INTEGER default 0 NOT NULL")
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `cameraValue` INTEGER default 0 NOT NULL")
            }
        }

        private val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `accountsValue` INTEGER default 0 NOT NULL")
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `phonestateValue` INTEGER default 0 NOT NULL")
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `playingcontentValue` INTEGER default 0 NOT NULL")
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `activityrecognitionValue` INTEGER default 0 NOT NULL")
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `networkstateValue` INTEGER default 0 NOT NULL")
                database.execSQL("ALTER TABLE `history_permission` ADD COLUMN `bodysensorsValue` INTEGER default 0 NOT NULL")
            }
        }

        private val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE permission_status")
                database.execSQL("DROP TABLE sensor_status")
            }
        }

            fun nullDatabase() {
            synchronized(PriVELTDatabase::class.java) { INSTANCE = null }
        }
    }
}