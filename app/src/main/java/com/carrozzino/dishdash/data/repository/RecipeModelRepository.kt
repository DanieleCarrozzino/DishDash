package com.carrozzino.dishdash.data.repository

import com.carrozzino.dishdash.data.database.RecipeModelDatabase
import com.carrozzino.dishdash.data.database.models.Meal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeModelRepository @Inject constructor(
    private val recipeDatabase: RecipeModelDatabase
) {

    fun all() : Flow<List<Meal>> {
        return recipeDatabase.RecipeModelDao().getAll()
    }

    fun getAll() : List<Meal> {
        return recipeDatabase.RecipeModelDao().getRecipes()
    }

    fun insert(recipe : Meal){
        recipeDatabase.RecipeModelDao().insert(recipe)
    }

    fun deleteAll(){
        recipeDatabase.RecipeModelDao().deleteAll()
    }

    fun delete(id : Int) {
        //recipeDatabase.RecipeModelDao().delete(id)
    }
}