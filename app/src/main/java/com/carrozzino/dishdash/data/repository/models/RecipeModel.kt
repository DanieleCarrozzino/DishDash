package com.carrozzino.dishdash.data.repository.models

import java.time.LocalDate

data class RecipeModel (
    val main : String = "",
    val mainIngredients : String = "",
    val side : String = "",
    val sideIngredients : String = "",
    val mainRecipe : String = "",
    val sideRecipe : String = "",
    val urlImage : String = "",
    val idImage : Int = 0
)

data class RecipeDayModel (
    val recipeModel: RecipeModel,
    val date : String
)