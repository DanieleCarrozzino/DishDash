package com.carrozzino.dishdash.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.data.database.models.MealPerDate
import com.carrozzino.dishdash.data.database.models.Meal
import com.carrozzino.dishdash.ui.screen.settings.TitleAndBackButton
import com.carrozzino.dishdash.ui.screen.utility.LazyColumnDragAndDrop
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.utility.ViewModelUtility
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import com.carrozzino.dishdash.ui.viewModels.UserIntent


@Composable
fun WeekListScreen (
    navController   : NavController,
    viewmodel       : MainViewModel,
    modifier        : Modifier,
) {
    val state = viewmodel.mainState.collectAsStateWithLifecycle().value
    WeekListCore(
        modifier = modifier,
        navController = navController,
        state = state,
        event = viewmodel::onReceive
    )
}

@Composable
fun WeekListCore (
    modifier        : Modifier      = Modifier,
    navController   : NavController = rememberNavController(),
    state           : MainState     = MainState(),
    event           : (UserIntent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column (modifier = modifier
            .fillMaxSize()) {

            TitleAndBackButton(
                title = "Reorder Weekly Meals",
                subtitle = "Long press an item to reorder it"
            ) { navController.navigateUp() }

            LazyColumnDragAndDrop(
                modifier    = Modifier.fillMaxSize(),
                list        = state.personalMeals,
                event       = {
                    event(UserIntent.OnReorderRecipesList(it))
                }
            ) { isDragging, index, recipe ->

                val elevation by animateDpAsState(if (isDragging) 4.dp else 1.dp)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .shadow(elevation = elevation, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                ) {

                    Row(modifier = Modifier) {
                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 10.dp, vertical = 12.dp)
                                .size(58.dp),
                            painter = painterResource(if(recipe.meal.idImage < ViewModelUtility.listImages.size)
                                ViewModelUtility.listImages[recipe.meal.idImage] else R.drawable.star,),
                            contentDescription = ""
                        )

                        Column(modifier    = Modifier
                            .padding(15.dp)
                            .weight(1f)) {
                            Text(
                                modifier    = Modifier.align(Alignment.Start),
                                color       = MaterialTheme.colorScheme.onBackground,
                                text        = ViewModelUtility.getDay(index),
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                modifier    = Modifier
                                    .padding(top = 5.dp)
                                    .align(Alignment.Start),
                                color       = MaterialTheme.colorScheme.onBackground,
                                text        = recipe.meal.main,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                modifier    = Modifier.align(Alignment.Start),
                                color       = MaterialTheme.colorScheme.onBackground,
                                text        = recipe.meal.mainIngredients,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }



                        Icon(
                            modifier    = Modifier
                                .padding(horizontal = 10.dp)
                                .align(Alignment.CenterVertically),
                            imageVector         = Icons.Outlined.Reorder,
                            contentDescription  = "",
                            tint                = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SingleRecipeWeekList(
    modifier : Modifier = Modifier,
    recipe : Meal = Meal()) {

}

@Preview()
@Composable
fun WeekListPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            WeekListCore(
                state = MainState(
                    personalMeals = listOf(
                        MealPerDate(
                            meal = Meal(main = "Rich plate 1"),
                            date = "today"
                        ),
                        MealPerDate(
                            meal = Meal(main = "Rich plate 2"),
                            date = "today"
                        ),
                        MealPerDate(
                            meal = Meal(main = "Rich plate 3"),
                            date = "today"
                        ),
                        MealPerDate(
                            meal = Meal(main = "Rich plate 4"),
                            date = "today"
                        )
                    )
                )
            )
        }
    )
}

@Preview()
@Composable
fun WeekListPreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            WeekListCore(
                state = MainState(
                    personalMeals = listOf(
                        MealPerDate(
                            meal = Meal(main = "Rich plate 1"),
                            date = "today"
                        ),
                        MealPerDate(
                            meal = Meal(main = "Rich plate 2"),
                            date = "today"
                        ),
                        MealPerDate(
                            meal = Meal(main = "Rich plate 3"),
                            date = "today"
                        ),
                        MealPerDate(
                            meal = Meal(main = "Rich plate 4"),
                            date = "today"
                        )
                    )
                )
            )
        }
    )
}