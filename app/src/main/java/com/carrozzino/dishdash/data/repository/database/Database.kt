package com.carrozzino.dishdash.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.carrozzino.dishdash.data.repository.database.dao.RecipeModelDao
import com.carrozzino.dishdash.data.repository.models.RecipeModel

@Database(entities = [RecipeModel::class], version = 6, exportSchema = false)
abstract class RecipeModelDatabase : RoomDatabase() {
    abstract fun RecipeModelDao() : RecipeModelDao
}