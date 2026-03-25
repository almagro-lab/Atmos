package com.almagro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.almagro.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert
    suspend fun insert(entity: WeatherEntity)

    @Query("SELECT * FROM weather_history ORDER BY id DESC LIMIT 10")
    fun getHistory(): Flow<List<WeatherEntity>>
}
