package com.carrozzino.dishdash.data.repository.database

import com.carrozzino.dishdash.data.repository.models.RecipeModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipeModelRepository @Inject constructor(
    private val recipeDatabase: RecipeModelDatabase
) {

    fun all() : Flow<List<RecipeModel>> {
        return recipeDatabase.RecipeModelDao().getAll()
    }

    fun getAll() : List<RecipeModel> {
        return recipeDatabase.RecipeModelDao().getRecipes()
    }

    fun insert(recipe : RecipeModel){
        recipeDatabase.RecipeModelDao().insert(recipe)
    }

    fun deleteAll(){
        recipeDatabase.RecipeModelDao().deleteAll()
    }

    fun delete(id : Int) {
        //recipeDatabase.RecipeModelDao().delete(id)
    }
}