package com.carrozzino.dishdash.ui.screen

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.data.repository.models.RecipeModel
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.White90
import com.carrozzino.dishdash.ui.utility.ViewModelUtility
import com.carrozzino.dishdash.ui.viewModels.UserIntent
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CalendarScreen(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>(),
    position : Int = 0
) {
    CalendarCore(
        modifier = modifier,
        navController = navController,
        viewModel = viewModel,
        position = position)
}

@Composable
fun CalendarCore(
    modifier: Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>(),
    position : Int = 0
) {

    val state = viewModel.mainState.collectAsState().value

    val pagerState = rememberPagerState(
        pageCount = { state.recipes.size },
        initialPageOffsetFraction = 0f,
        initialPage = if(position < state.recipes.size) position else 0)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPageOffsetFraction }.collect { page ->
            Log.d("Page change", "Page changed to $page")
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()) {

        HorizontalPager(state = pagerState) { page ->
            println("Page loaded $page")

            if(state.recipes.size < page) return@HorizontalPager

            CalendarSingleCoreFullScreen (
                modifier    = modifier,
                recipe      = state.recipes[page].recipeModel,
                date        = state.recipes[page].date,
                today       = state.actualDate,
                action      = viewModel::onReceive
            ) {
                navController.navigate(Screen.Home.route)
            }
        }
    }
}

@Composable
fun FoodAvatar(
    modifier    : Modifier = Modifier,
    list        : List<Int> = ViewModelUtility.listImages,
    id          : Int = 0,
    small       : Boolean = false,
) {
    val offsetXSize = 20
    val offsetYSize = 10

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
        )
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val deltaSize = if(small) 3.5f else 2.5f

    val rad = Math.toRadians(angle.toDouble())
    val offsetX = cos(rad).toFloat()
    val offsetY = sin(rad).toFloat()

    Column(modifier = modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier
                .size(screenWidth / deltaSize)
                .align(Alignment.CenterHorizontally)
                .offset(x = (offsetX * offsetXSize).dp, y = (offsetY * offsetYSize).dp)
                .graphicsLayer(
                    rotationZ = 10 * offsetX
                ),
            painter = painterResource(list[id]),
            contentDescription = ""
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .blur(30.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .offset(x = (offsetX * (offsetXSize / 2)).dp, y = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .size(width = (screenWidth / (deltaSize + 2)), height = 20.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

@Composable
fun BottomButtons(
    modifier    : Modifier = Modifier,
    recipe      : RecipeModel = RecipeModel(),
    action      : (UserIntent) -> Unit = {}
) {
    Row(modifier = modifier) {
        Button(
            modifier = Modifier.padding(
                start = 18.dp,
                end = 9.dp,
                top = 10.dp,
                bottom = 10.dp
            ).weight(1f),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            onClick = {
                // TODO
            }) {
            Icon(
                Icons.Filled.Create,
                contentDescription = "modify button",
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = White90
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "modify", color = White90)
        }

        Button(
            modifier = Modifier.padding(
                start = 9.dp,
                end = 18.dp,
                top = 10.dp,
                bottom = 10.dp
            ),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
            ),
            onClick = {
                action(UserIntent.OnOpenLinkRecipe(recipe.link))
            }) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "open recipe", color = White90)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "open recipe button",
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = White90
            )
        }
    }
}

@Composable
fun CalendarSingleCoreFullScreen(
    modifier : Modifier = Modifier,
    recipe : RecipeModel = RecipeModel(),
    date : String = "",
    today : String = "",
    action : (UserIntent) -> Unit = {},
    navigate : () -> Unit = {}
) {
    val isToday = date == today

    Box(modifier = Modifier.fillMaxSize().background(
        ViewModelUtility.getColorFromId(recipe.idImage, isSystemInDarkTheme()).copy(
            alpha = if(isToday) 1f else 0.5f))) {
        CalendarSingleCore(
            modifier    = modifier,
            recipe      = recipe,
            date        = date,
            today       = today,
            navigate    = navigate,
            action      = action,
        )
    }
}

@Composable
fun CalendarSingleCore(
    modifier : Modifier = Modifier,
    recipe : RecipeModel = RecipeModel(),
    date : String = "",
    today : String = "",
    action      : (UserIntent) -> Unit = {},
    navigate    : () -> Unit = {}
) {
    val isToday = date == today

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier
            .padding(vertical = 25.dp, horizontal = 20.dp)) {
            Row {
                if(isToday)
                    Image(
                        modifier = Modifier.size(24.dp).padding(end = 5.dp),
                        painter = painterResource(R.drawable.star),
                        contentDescription = ""
                    )

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = if(isToday) "Recipe of Today" else date,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                modifier = Modifier,
                text = recipe.main,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Box(modifier = Modifier
            .padding(20.dp)
            .align(Alignment.TopEnd)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable{ navigate() }) {
            Icon(
                modifier = Modifier.padding(10.dp),
                imageVector = Icons.Rounded.Clear,
                contentDescription = "",
                tint = White90
            )
        }

        FoodAvatar(
            modifier = Modifier.align(Alignment.Center).alpha(if(isToday) 1f else 0.6f),
            id = recipe.idImage
        )

        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 15.dp)) {

            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = recipe.mainIngredients.replace("\n", ", "),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            if(recipe.side.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "Side",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "${recipe.side} - ${recipe.sideIngredients}",
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            BottomButtons(
                modifier = Modifier,
                recipe = recipe,
                action = action
            )
        }
    }
}

@Composable
fun CalendarSingleCoreMinimal(
    modifier : Modifier = Modifier,
    recipe : RecipeModel = RecipeModel(),
    date : String = "",
    today : String = "",
    click : () -> Unit = {}
) {
    val isToday = date == today

    Box(modifier = modifier.fillMaxSize().clickable {
        click()
    }) {

        FoodAvatar(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(if(isToday) 1f else 0.6f),
            id = recipe.idImage,
            small = true
        )

        Column(modifier = Modifier
            .padding(vertical = 25.dp, horizontal = 20.dp)) {
            Row {
                if(isToday)
                    Image(
                        modifier = Modifier.size(24.dp).padding(end = 5.dp),
                        painter = painterResource(R.drawable.star),
                        contentDescription = ""
                    )

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = if(isToday) "Recipe of Today" else date,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                modifier = Modifier,
                text = recipe.main,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(vertical = 25.dp, horizontal = 20.dp)) {

            Text(
                modifier = Modifier,
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                modifier = Modifier,
                text = recipe.mainIngredients.replace("\n", ", "),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            if(recipe.side.isNotEmpty()) {
                Text(
                    modifier = Modifier,
                    text = "Side",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier,
                    text = "${recipe.side} - ${recipe.sideIngredients}",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun NewActionButton(
    modifier : Modifier = Modifier,
    imageVector : ImageVector? = null,
    painter: Painter? = null,
    background : Color = MaterialTheme.colorScheme.primary,
    click : () -> Unit = {}
) {
    Box(modifier = modifier
        .clip(CircleShape)
        .background(background)
        .size(70.dp)
        .clickable{
            click()
        }
    ) {
        if(imageVector != null) {
            Icon(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxSize()
                    .align(Alignment.Center),
                imageVector = imageVector,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.background
            )
        }
        else if(painter != null) {
            Icon(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxSize()
                    .align(Alignment.Center),
                painter = painter,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.background)
        }
    }
}

@Preview
@Composable
fun AddNewRecipeButtonPreview() {
    NewActionButton()
}


@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    CalendarSingleCore(recipe = RecipeModel(
        main = "Daniele",
        side = "Carrozzino"
    ))
}