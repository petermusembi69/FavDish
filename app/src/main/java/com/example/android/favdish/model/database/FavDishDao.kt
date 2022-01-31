package com.example.android.favdish.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.android.favdish.model.entities.FavDish

@Dao
interface FavDishDao {

    @Insert
    suspend fun insetFavDishDetails(favDish: FavDish)

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Delete
    suspend fun deleteFavDishDetails(favDish: FavDish)
}