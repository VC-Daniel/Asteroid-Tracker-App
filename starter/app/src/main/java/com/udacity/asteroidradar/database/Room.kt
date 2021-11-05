package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.*

/** Stores information about asteroids that were retrieved from the NASA api */
@Database(entities = [DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

/** Specifies available interactions with the asteroid database */
@Dao
interface AsteroidDao {
    /** Get the asteroid data with the specified [closeApproach]*/
    @Query("select * from DatabaseAsteroid where closeApproachDate = :closeApproach order by closeApproachDate")
    fun getTodaysAsteroids(closeApproach: String): List<DatabaseAsteroid>

    /** Get 7 days worth of asteroid data beginning with the specified [closeApproach]*/
    @Query("select * from DatabaseAsteroid where closeApproachDate >= :closeApproach order by closeApproachDate")
    fun getWeeksAsteroids(closeApproach: String): List<DatabaseAsteroid>

    /** Get all the stored asteroid data ordered by closeApproachDate*/
    @Query("select * from DatabaseAsteroid order by closeApproachDate")
    fun getAllAsteroids(): List<DatabaseAsteroid>

    /** Insert new asteroids into the database*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    /** Remove any records that are from before [today]*/
    @Query("delete from DatabaseAsteroid where closeApproachDate < :today")
    fun removeOldData(today: String): Int
}

private lateinit var INSTANCE: AsteroidDatabase
private const val DATABASE_NAME = "asteroids"

/** Returns the asteroid database*/
fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
    return INSTANCE
}