package com.example.android.favdish.model.database

import androidx.room.*
import com.example.android.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insetFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1 ORDER BY ID")
    fun getFavoriteDishesList(): Flow<List<FavDish>>

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDish)
}