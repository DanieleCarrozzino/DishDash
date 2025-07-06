package com.carrozzino.dishdash.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.carrozzino.dishdash.data.database.dao.RecipeModelDao
import com.carrozzino.dishdash.data.database.models.RecipeModel

@Database(entities = [RecipeModel::class], version = 6, exportSchema = false)
abstract class RecipeModelDatabase : RoomDatabase() {
    abstract fun RecipeModelDao() : RecipeModelDao
}