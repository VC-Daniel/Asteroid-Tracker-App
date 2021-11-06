package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.ImageOfTheDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

/** The status of an api call */
enum class AsteroidApiStatus { LOADING, ERROR, DONE }

/** The logic for the main fragment. */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    /** internal status of the image of the day api call*/
    private val _status = MutableLiveData<AsteroidApiStatus>()

    /** Status of the api call to retrieve asteroid data*/
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    /** Internal status of the image of the day api call to*/
    private val _imageStatus = MutableLiveData<AsteroidApiStatus>()

    /** Status of the image of the day api call to*/
    val imageStatus: LiveData<AsteroidApiStatus>
        get() = _imageStatus

    /** Internal image of the day data*/
    private val _imageOfTheDay = MutableLiveData<ImageOfTheDay>()

    /** Image of the day data*/
    val imageOfTheDay: LiveData<ImageOfTheDay>
        get() = _imageOfTheDay

    /** Internal selected asteroid*/
    private val _asteroidToNavigateTo = MutableLiveData<Asteroid>()

    /** Selected asteroid*/
    val asteroidToNavigateTo: LiveData<Asteroid>
        get() = _asteroidToNavigateTo

    // Get the database and repository to interact with the asteroid data
    private val database = getDatabase(app)
    private val asteroidRepository = AsteroidRepository(database)

    // The external LiveData for the asteroids to display
    val asteroids = asteroidRepository.asteroids

    init {
        // Get the asteroid data for the next 7 days from the api and insert it into the database
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroidData(BuildConfig.NASA_API_KEY)
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
            }
        }

        // Get the latest asteroid data from the database
        viewModelScope.launch {
            asteroidRepository.getWeeksAsteroids()
        }

        // Retrieve the image of the day data from the api
        getImageOfTheDay()
    }

    /** Retrieves the image of the day data from the NASA api*/
    private fun getImageOfTheDay() {
        _imageStatus.value = AsteroidApiStatus.LOADING

        viewModelScope.launch {
            try {
                _imageOfTheDay.value = AsteroidApi.retrofitService.getImageOfTheDay(
                    BuildConfig.NASA_API_KEY
                )
                _imageStatus.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _imageStatus.value = AsteroidApiStatus.ERROR
            }
        }
    }

    /** Set the asteroid that was selected to display more details about*/
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _asteroidToNavigateTo.value = asteroid
    }

    /** Signifies the details of the selected asteroid have been displayed*/
    fun displayAsteroidDataComplete() {
        _asteroidToNavigateTo.value = null
    }

    /** Filter the asteroids to display to only those with a close approach of today*/
    fun showTodaysAsteroids() {
        viewModelScope.launch {
            asteroidRepository.getTodaysAsteroids()
        }
    }

    /** Filter the asteroids to display to only those with a close approach within the next 7 days*/
    fun showWeeksAsteroids() {
        viewModelScope.launch {
            asteroidRepository.getWeeksAsteroids()
        }
    }

    /** Retrieve all asteroids in the database*/
    fun showAllAsteroids() {
        viewModelScope.launch {
            asteroidRepository.getAllAsteroids()
        }
    }

    /** denotes the asteroid api issue has been displayed*/
    fun displayAsteroidApiIssueComplete() {
        _status.value = AsteroidApiStatus.DONE
    }

    /** denotes the image of the day api issue has been displayed*/
    fun displayImageApiIssueComplete() {
        _imageStatus.value = AsteroidApiStatus.DONE
    }

}