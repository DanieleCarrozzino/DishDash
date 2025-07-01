package com.carrozzino.dishdash.data.repository.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class RecipeModel (
    @PrimaryKey() val id: Int = 0,
    val main : String = "",
    val mainIngredients : String = "",
    val link : String = "",
    val side : String = "",
    val sideIngredients : String = "",
    val mainRecipe : String = "",
    val sideRecipe : String = "",
    val urlImage : String = "",
    val idImage : Int = 0
)

@Keep
data class RecipeDayModel (
    val recipeModel: RecipeModel,
    val date : String
)