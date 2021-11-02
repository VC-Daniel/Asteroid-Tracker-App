package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
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
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external immutable LiveData for the request status
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        getAsteroidData(app)
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
                _status.value = AsteroidApiStatus.ERROR
            }
        }
    }
}