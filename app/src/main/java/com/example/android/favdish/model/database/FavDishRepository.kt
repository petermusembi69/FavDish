package com.example.android.favdish.model.database

import androidx.annotation.WorkerThread
import com.example.android.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insetFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish) {
        favDishDao.updateFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish) {
        favDishDao.deleteFavDishDetails(favDish)
    }

    val allDishesList : Flow<List<FavDish>> = favDishDao.getAllDishesList()

    val favoriteDishesList : Flow<List<FavDish>> = favDishDao.getFavoriteDishesList()

}