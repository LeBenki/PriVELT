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

@Database(entities = [UserData::class, Service::class, CurrentUser::class, Settings::class, SensorStatus::class], version = 5, exportSchema = false)
abstract class PriVELTDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao?
    abstract fun serviceDao(): ServiceDao?
    abstract fun currentUserDao(): CurrentUserDao?
    abstract fun settingsDao(): SettingsDao?
    abstract fun sensorStatusDao(): SensorStatusDao?

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
                                .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
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
        fun nullDatabase() {
            synchronized(PriVELTDatabase::class.java) { INSTANCE = null }
        }
    }
}