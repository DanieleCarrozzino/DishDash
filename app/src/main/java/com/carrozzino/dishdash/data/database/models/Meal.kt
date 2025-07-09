package com.carrozzino.dishdash.data.database.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Keep
data class Meal (
    @PrimaryKey() val id: Int = 0,
    val main : String = "",
    val mainIngredients : String = "",
    val link : String = "",
    val isVegetarian : Boolean = false,
    val side : String = "",
    val sideIngredients : String = "",
    val mainRecipe : String = "",
    val sideRecipe : String = "",
    val urlImage : String = "",
    val idImage : Int = 0,
    val serverId : Int = -1
)

@Keep
data class MealPerDate (
    val meal    : Meal,
    val date    : String
)