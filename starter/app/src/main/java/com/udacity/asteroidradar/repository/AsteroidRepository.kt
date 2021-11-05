package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/** Facilitates interaction with the asteroid data in the repository. The repository uses a database
 *  for caching data and uses the NASA api to get the latest data*/
enum class RepositoryFilter { ALL, WEEK, TODAY }
class AsteroidRepository(private val database: AsteroidDatabase) {

    /** Internal version of the asteroid data to be presented to the user
     * update this with the latest asteroid data based on the specified filter*/
    private var _asteroids = MutableLiveData<List<Asteroid>>()

    /** The asteroid data to be presented to the user*/
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    /** The currently Selected filter for which data to display*/
    var repositoryFilter = RepositoryFilter.WEEK

    // Used for logging any error messages
    private val className = "AsteroidRepository"
    private val databaseExceptionFormat = "Error when saving new data to the database: %s"
    private val databaseExceptionRemovingDataFormat =
        "Error when deleting data from the database: %s"

    /** Retrieve the latest data from the NASA api and update the database*/
    suspend fun refreshAsteroidData(apiToken: String) {
        withContext(Dispatchers.IO) {
            try {
                // Call the api to get the asteroid data for the next 7 days
                // and update the database data
                val response = AsteroidApi.retrofitService.getAsteroids(
                    getToday(), apiToken
                )

                // parse the response to usable asteroids
                val jsonObject = JSONObject(response)
                val asteroids = parseAsteroidsJsonResult(jsonObject)

                // Insert the asteroid data we retrieved from the api
                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())

                // display the latest data that matches the specified filter
                when (repositoryFilter) {
                    RepositoryFilter.ALL -> getAllAsteroids()
                    RepositoryFilter.TODAY -> getToday()
                    else -> getWeeksAsteroids()
                }
            } catch (e: Exception) {
                Log.e(
                    className,
                    String.format(
                        databaseExceptionFormat,
                        e.message.toString()
                    )
                )
            }
        }
    }

    /** Remove any asteroid data that has a close approach of before today*/
    suspend fun removeOldAsteroidData() {
        withContext(Dispatchers.IO) {
            try {
                database.asteroidDao.removeOldData(getToday())
            } catch (e: Exception) {
                Log.e(
                    className,
                    String.format(
                        databaseExceptionRemovingDataFormat,
                        e.message.toString()
                    )
                )
            }
        }
    }

    /** Retrieve all asteroid data from the repository*/
    suspend fun getAllAsteroids() {
        repositoryFilter = RepositoryFilter.ALL
        withContext(Dispatchers.IO) {
            // Using postValue to update the live data on a background thread instead of setting
            // the live data value as described in https://stackoverflow.com/a/60126585
            _asteroids.postValue(database.asteroidDao.getAllAsteroids().asDomainModel())
        }
    }

    /** Retrieve all asteroid data with a close approach withing a week from the repository*/
    suspend fun getWeeksAsteroids() {
        repositoryFilter = RepositoryFilter.WEEK
        withContext(Dispatchers.IO) {
            _asteroids.postValue(database.asteroidDao.getWeeksAsteroids(getToday()).asDomainModel())
        }
    }

    /** Retrieve all asteroid data that has a close approach of today from the repository*/
    suspend fun getTodaysAsteroids() {
        repositoryFilter = RepositoryFilter.TODAY
        withContext(Dispatchers.IO) {
            _asteroids.postValue(
                database.asteroidDao.getTodaysAsteroids(getToday()).asDomainModel()
            )
        }
    }

    /** Get today's date formatted in the standard way used in the database and the api*/
    private fun getToday(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}