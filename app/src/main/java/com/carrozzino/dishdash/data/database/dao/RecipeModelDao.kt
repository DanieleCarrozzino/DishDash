package com.carrozzino.dishdash.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.carrozzino.dishdash.data.database.models.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeModelDao {

    @Query("SELECT * FROM Meal")
    fun getAll() : Flow<List<Meal>>

    @Query("SELECT * FROM Meal")
    fun getRecipes() : List<Meal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(group : Meal)

    @Query("DELETE FROM Meal")
    fun deleteAll()

//    @Query("DELETE FROM `RecipeModel` WHERE id == :id")
//    fun delete(id : Int)

}