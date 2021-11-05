package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

/** Retrieve the latest data from the NASA api and removes old asteroid data from the repository*/
class RefreshAsteroidDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshAsteroidDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        val apiToken = applicationContext.getString(R.string.apiToken)

        // get the latest data and remove old data
        return try {
            repository.refreshAsteroidData(apiToken)
            repository.removeOldAsteroidData()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}