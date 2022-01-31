package com.example.android.favdish.application

import android.app.Application
import com.example.android.favdish.model.database.FavDishRepository
import com.example.android.favdish.model.database.FavDishRoomDatabase

class FavDishApplication :  Application() {

    private val database by lazy { FavDishRoomDatabase.getDatabse(this@FavDishApplication)}

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}