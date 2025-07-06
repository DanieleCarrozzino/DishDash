package com.carrozzino.dishdash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.data.database.models.RecipeDayModel
import com.carrozzino.dishdash.data.database.models.RecipeModel
import com.carrozzino.dishdash.ui.screen.settings.TitleAndBackButton
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Absorb clicks */ }
    ) {
        LazyColumn (modifier = modifier.fillMaxSize()) {

            item {
                TitleAndBackButton(
                    title = "Modify your week"
                ) { navController.navigateUp() }
            }

            items(state.recipes) {

            }
        }
    }
}

@Composable
fun SingleRecipeWeekList(
    modifier : Modifier = Modifier,
    recipe : RecipeModel = RecipeModel()) {

}

@Preview()
@Composable
fun WeekListPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            WeekListCore(
                state = MainState(
                    recipes = listOf(
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        ),
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        ),
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        ),
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
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
                    recipes = listOf(
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        ),
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        ),
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        ),
                        RecipeDayModel(
                            recipeModel = RecipeModel(main = "Rich plate"),
                            date = "today"
                        )
                    )
                )
            )
        }
    )
}