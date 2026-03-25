package com.almagro.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.almagro.data.dao.WeatherDao
import com.almagro.data.model.WeatherEntity

@Database(entities = [WeatherEntity::class], version = 1)
abstract class AtmosDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}
