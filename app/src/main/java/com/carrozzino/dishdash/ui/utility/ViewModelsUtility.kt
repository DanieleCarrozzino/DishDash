package com.carrozzino.dishdash.ui.utility

import android.util.Base64
import androidx.compose.ui.graphics.Color
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.theme.Back1
import com.carrozzino.dishdash.ui.theme.Back2
import com.carrozzino.dishdash.ui.theme.Back3
import com.carrozzino.dishdash.ui.theme.Back4
import com.carrozzino.dishdash.ui.theme.Back5
import com.carrozzino.dishdash.ui.theme.BackDark1
import com.carrozzino.dishdash.ui.theme.BackDark2
import com.carrozzino.dishdash.ui.theme.BackDark3
import com.carrozzino.dishdash.ui.theme.BackDark4
import com.carrozzino.dishdash.ui.theme.BackDark5
import com.carrozzino.dishdash.ui.theme.OnBack4
import com.carrozzino.dishdash.ui.theme.OnBack5
import com.carrozzino.dishdash.ui.theme.OnBackDark4
import com.carrozzino.dishdash.ui.theme.OnBackDark5
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class ViewModelUtility() {
    companion object {

        const val RECIPE_MODULE = "recipe_of_the_week"

        private val formatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("EEEE dd MMMM")

        fun getWeek(): List<String> {
            var today = LocalDate.now()
            today = today.minusDays(today.dayOfWeek.ordinal.toLong())

            return (0..DayOfWeek.SUNDAY.ordinal)
                .map { today.plusDays(it.toLong()) }
                .map { it.format(formatter) }
        }

        fun getActualDate(): String {
            val today = LocalDate.now()
            return today.format(formatter)
        }

        val listColors : List<Color> = listOf(
            Back1, Back2, Back3, Back4, Back5
        )

        val listColorsDark : List<Color> = listOf(
            BackDark1, BackDark2, BackDark3, BackDark4, BackDark5
        )

        val listImages : List<Int> = listOf(
            R.drawable.hamburger, R.drawable.salad, R.drawable.burrito, R.drawable.spaghetti,
            R.drawable.focaccia, R.drawable.lasagna, R.drawable.meat, R.drawable.pizza, R.drawable.pasta,
            R.drawable.gnocchi, R.drawable.poke, R.drawable.vellutata, R.drawable.bread, R.drawable.chicken,
            R.drawable.meatballs, R.drawable.trofie, R.drawable.couscous, R.drawable.caprese, R.drawable.hummus,
            R.drawable.toast, R.drawable.bruschetta, R.drawable.lasagne_al_pesto
        )

        fun getColorFromId(id : Int, dark : Boolean = false) : Color {
            val index = abs(id % listColors.size)
            return if(dark) listColorsDark[index] else listColors[index]
        }

        fun getColorFromType(isVegetarian : Boolean, dark : Boolean = false) : Color {
            return if(dark && isVegetarian) BackDark4 else if(dark) BackDark5 else if(isVegetarian) Back4 else Back5
        }

        fun getColorFromTypeOver(isVegetarian : Boolean, dark : Boolean = false) : Color {
            return if(dark && isVegetarian) OnBackDark4 else if(dark) OnBackDark5 else if(isVegetarian) OnBack4 else OnBack5
        }

        fun encodeToBase64(input: String): String {
            val bytes = input.toByteArray(Charsets.UTF_8)
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        fun getDay(index : Int) : String {
            return when(index) {
                0 -> "Monday"
                1 -> "Tuesday"
                2 -> "Wednesday"
                3 -> "Thursday"
                4 -> "Friday"
                5 -> "Saturday"
                6 -> "Sunday"
                else -> "default"
            }
        }
    }
}