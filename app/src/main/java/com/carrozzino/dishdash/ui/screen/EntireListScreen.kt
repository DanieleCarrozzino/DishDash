package com.carrozzino.dishdash.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.data.database.models.Meal
import com.carrozzino.dishdash.data.database.models.MealPerDate
import com.carrozzino.dishdash.ui.screen.settings.TitleAndBackButton
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.utility.ViewModelUtility
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import com.carrozzino.dishdash.ui.viewModels.Recipe
import com.carrozzino.dishdash.ui.viewModels.UserIntent


@Composable
fun EntireListScreen (
    navController   : NavController,
    viewmodel       : MainViewModel,
    modifier        : Modifier,
    position        : Int = 0,
) {
    val state = viewmodel.mainState.collectAsStateWithLifecycle().value
    println("Daniele | $position")
    EntireListCore(
        modifier = modifier,
        navController = navController,
        state = state,
        mealToChange = state.personalMeals[position].meal,
        indexToChange = position,
        event = viewmodel::onReceive
    )
}

@Composable
fun EntireListCore (
    modifier        : Modifier      = Modifier,
    navController   : NavController = rememberNavController(),
    state           : MainState     = MainState(),
    mealToChange    : Meal          = Meal(),
    indexToChange   : Int           = 0,
    event           : (UserIntent) -> Unit = {}
) {
    var filter by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn (
            modifier    = modifier.fillMaxSize(),
            contentPadding      = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            stickyHeader {
                HeaderView(
                    navController = navController
                ) {
                    filter = it
                }
            }

            items(state.totalRecipes
                .filter {
                    it.title.lowercase().startsWith(filter.lowercase()) ||
                    it.title.lowercase().contains(filter.lowercase())
                }
                .sortedBy { it.title }
            ) { recipe ->
                SingleRecipe(
                    recipe = recipe,
                    filter = filter
                ) {
                    event(UserIntent.OnChangeSingleRecipe(
                        autoGenerate    = false,
                        indexToChange   = indexToChange,
                        mealToAdd       = recipe.toMeal()))
                    filter = ""
                    navController.navigateUp()
                }
            }
        }
    }
}

@Composable
fun SingleRecipe(
    recipe : Recipe,
    filter : String = "",
    click : () -> Unit = {}
) {
    var annotatedString : AnnotatedString = buildAnnotatedString {
        append(recipe.title)
    }

    if(filter.isNotEmpty()) {
        val startIndex = recipe.title.lowercase().indexOf(filter)

        val endIndex = startIndex + filter.length
        val highlightStyle = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        annotatedString = buildAnnotatedString {
            append(recipe.title.substring(0, startIndex))
            withStyle(style = highlightStyle) {
                append(recipe.title.substring(startIndex, endIndex))
            }
            append(recipe.title.substring(endIndex))
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 5.dp)
        .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surface)
        .clickable {
            click()
        }
    ) {

        Row(modifier = Modifier) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .size(58.dp),
                painter = painterResource(if(recipe.idImage < ViewModelUtility.listImages.size)
                    ViewModelUtility.listImages[recipe.idImage] else R.drawable.star,),
                contentDescription = ""
            )

            Column(modifier    = Modifier
                .padding(15.dp)
                .weight(1f)) {

                Text(
                    modifier    = Modifier
                        .padding(top = 5.dp)
                        .align(Alignment.Start),
                    color       = MaterialTheme.colorScheme.onBackground,
                    text        = annotatedString,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier    = Modifier.align(Alignment.Start),
                    color       = MaterialTheme.colorScheme.onBackground,
                    text        = recipe.ingredients,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HeaderView(
    navController : NavController = rememberNavController(),
    searchCallback : (String) -> Unit = {}
){
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background))
    {
        TitleAndBackButton(
            title = "Plan Your Meals",
            subtitle = "Select recipes to include in your weekly schedule"
        ) { navController.navigateUp() }

        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 18.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)),
        ) {

            Icon(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                    .size(30.dp)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Rounded.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Lens"
            )

            BasicTextField(
                value = textState,
                onValueChange = { it ->
                    textState = it
                    searchCallback(it.text) },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                cursorBrush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.onBackground)
                ),
                maxLines = 1,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (textState.text.isEmpty()) {
                            Text(
                                text = "Search name or type",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
fun SingleRecipeEntireList(
    modifier : Modifier = Modifier,
    recipe : Meal = Meal()) {

}

@Preview()
@Composable
fun EntireListPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            EntireListCore(
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
fun EntireListPreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            EntireListCore(
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