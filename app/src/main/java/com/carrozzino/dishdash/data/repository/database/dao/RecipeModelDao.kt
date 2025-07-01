package com.carrozzino.dishdash.data.repository.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.carrozzino.dishdash.data.repository.models.RecipeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeModelDao {

    @Query("SELECT * FROM `RecipeModel`")
    fun getAll() : Flow<List<RecipeModel>>

    @Query("SELECT * FROM `RecipeModel`")
    fun getRecipes() : List<RecipeModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(group : RecipeModel)

    @Query("DELETE FROM `RecipeModel`")
    fun deleteAll()

//    @Query("DELETE FROM `RecipeModel` WHERE id == :id")
//    fun delete(id : Int)

}