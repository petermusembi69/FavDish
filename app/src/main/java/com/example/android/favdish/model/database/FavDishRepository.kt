package com.example.android.favdish.model.database

import androidx.annotation.WorkerThread
import com.example.android.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insetFavDishDetails(favDish)
    }

    val allDishesList : Flow<List<FavDish>> = favDishDao.getAllDishesList()
}