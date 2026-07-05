package com.example.vip_mobile.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vip_mobile.data.dao.IBSDao
import com.example.vip_mobile.data.entity.*
import net.sqlcipher.database.SupportFactory

@Database(entities = [Nasabah::class, Rekening::class, Transaksi::class], version = 1)
abstract class IBSDatabase : RoomDatabase() {
    abstract fun ibsDao(): IBSDao

    companion object {
        @Volatile
        private var INSTANCE: IBSDatabase? = null

        fun getInstance(context: Context, password: String): IBSDatabase {
            return INSTANCE ?: synchronized(this) {
                val factory = SupportFactory(password.toByteArray())
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IBSDatabase::class.java,
                    "ibs_core_secure.db"
                ).openHelperFactory(factory).build()
                INSTANCE = instance
                instance
            }
        }
    }
}