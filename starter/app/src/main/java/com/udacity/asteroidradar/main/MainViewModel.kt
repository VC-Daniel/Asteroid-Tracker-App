package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(app: Application) : AndroidViewModel(app) {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    // The internal MutableLiveData that stores the status of the most recent request
    private val _imageStatus = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status
    val imageStatus: LiveData<AsteroidApiStatus>
        get() = _imageStatus

    // The internal MutableLiveData that stores the status of the most recent request
    private val _imageOfTheDay = MutableLiveData<PictureOfDay>()

    // The external immutable LiveData for the request status
    val imageOfTheDay: LiveData<PictureOfDay>
        get() = _imageOfTheDay

    // The internal MutableLiveData that stores the status of the most recent request
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external immutable LiveData for the request status
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    // The internal MutableLiveData that stores the status of the most recent request
    private val _asteroidToNavigateTo = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the request status
    val asteroidToNavigateTo: LiveData<Asteroid>
        get() = _asteroidToNavigateTo

    init {
        getAsteroidData(app)
        getImageOfTheDay(app)
    }

    private fun getImageOfTheDay(app: Application) {
        _imageStatus.value = AsteroidApiStatus.LOADING

        viewModelScope.launch {
            try {
                _imageOfTheDay.value = AsteroidApi.retrofitService.getImageOfTheDay(
                    app.applicationContext.getString(R.string.apiToken)
                )
                _imageStatus.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _imageStatus.value = AsteroidApiStatus.ERROR
            }
        }
    }

    private fun getAsteroidData(app: Application) {

        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat =
            SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        _status.value = AsteroidApiStatus.LOADING

        viewModelScope.launch {
            try {
                var response = AsteroidApi.retrofitService.getAsteroids(
                    dateFormat.format(currentTime),
                    app.applicationContext.getString(R.string.apiToken)
                )
                val jsonObject = JSONObject(response)
                _asteroids.value = parseAsteroidsJsonResult(jsonObject)
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                Log.i("MainViewModel", e.message.toString())
                _status.value = AsteroidApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _asteroidToNavigateTo.value = asteroid
    }

    fun displayAsteroidDataComplete() {
        _asteroidToNavigateTo.value = null
    }

}