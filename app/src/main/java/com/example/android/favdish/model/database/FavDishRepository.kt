package com.example.android.favdish.model.database

import androidx.annotation.WorkerThread
import com.example.android.favdish.model.entities.FavDish

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insetFavDishDetails(favDish)
    }
}