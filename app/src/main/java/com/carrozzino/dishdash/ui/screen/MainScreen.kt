package com.carrozzino.dishdash.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.theme.Typography
import com.carrozzino.dishdash.ui.utility.ViewModelUtility
import com.carrozzino.dishdash.ui.viewModels.UserIntent
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainStatus
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>()
) {
    val state = viewModel.mainState.collectAsState().value

    MainCore(
        modifier = modifier,
        navController = navController,
        state = state) {
        viewModel.onReceive(it)
    }
}

@Composable
fun MainCore(
    modifier: Modifier = Modifier,
    navController : NavController = rememberNavController(),
    state : MainState = MainState(),
    click : (userIntent : UserIntent) -> Unit = {}
    ) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
//        LoadingVegetables(modifier = Modifier.blur(20.dp))

        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(vertical = 25.dp, horizontal = 20.dp)) {
                Row {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 5.dp),
                        painter = painterResource(R.drawable.pizza),
                        contentDescription = ""
                    )

                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = "Whatever you need",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    modifier = Modifier,
                    text = "Dish Dash Bish",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )


                Row(modifier = Modifier) {
                    ButtonDescriptionAndSubDescription(
                        modifier    = Modifier
                            .weight(1f)
                            .padding(end = 6.dp),
                        description = "Found a new recipe?",
                        sub         = "Adding a new recipe to your virtual cook book!",
                        image       = R.drawable.glass,
                    ) {
                        navController.navigate(Screen.Adding.route)
                    }

                    ButtonDescriptionAndSubDescription(
                        modifier    = Modifier
                            .weight(1f)
                            .padding(start = 6.dp),
                        description = "Need a new week?",
                        sub         = "Generate a new menu for your awesome week!",
                        image       = R.drawable.calendar,
                    ) {
                        navController.navigate(Screen.Generate.route)
                        click(UserIntent.OnGenerateNewWeek)
                    }
                }

                    AnimatedContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 12.dp)
                            .weight(1f)
                        ,
                        targetState = state.state,
                        label = "animated content"
                    ) { targetState ->

                        Box(modifier = Modifier.fillMaxSize()) {
                            when(targetState) {
                                MainStatus.DEFAULT -> {
                                    CircularProgressIndicator(
                                        progress = { 100f },
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(40.dp)
                                    )
                                }

                                MainStatus.INITIALIZED -> {
                                    HorizontalWeek(
                                        state = state,
                                        navController = navController
                                    )
                                }

                                MainStatus.REFRESHING -> {

                                }

                                MainStatus.EMPTY -> {
                                    PlaceHolderEmptyWeek(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun PlaceHolderEmptyWeek(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FoodAvatar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            list = listOf<Int>(R.drawable.pot),
            id = 0,
            small = false
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = "You need to generate a new week!",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun HorizontalWeek(
    state : MainState = MainState(),
    navController: NavController = rememberNavController()
) {
    val margin = 0
    val customPageSize = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return (availableSpace - 2 * pageSpacing)
        }
    }

    val pagerState = rememberPagerState(
        pageCount = { state.recipes.size },
        initialPage = 0.coerceAtLeast(state.recipes.indexOfFirst { it.date == state.actualDate })
    )

    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            pageSpacing = 0.dp,
            pageSize = customPageSize,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(0)
            ),
            contentPadding = PaddingValues(horizontal = margin.dp),
        ) { page ->
            CalendarSingleCoreMinimal(
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        alpha = lerp(
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleY = lerp(
                            start = 0.75f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleX = lerp(
                            start = 0.75f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        ViewModelUtility.getColorFromId(
                            state.recipes[page].recipeModel.idImage,
                            isSystemInDarkTheme()
                        ).copy(alpha = 0.6f)
                    ),
                recipe = state.recipes[page].recipeModel,
                date = state.recipes[page].date,
                today = state.actualDate
            ) {
                navController.navigate(Screen.Calendar.route)
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)) {
            repeat(state.recipes.size) { index ->
                SingleDaySelector(
                    modifier = Modifier.weight(1f),
                    text = state.recipes[index].date.substring(0, 2),
                    selected = index == pagerState.currentPage,
                    actualDate = state.recipes[index].date == state.actualDate
                ) {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        }
    }
}

@Composable
fun SingleDaySelector(
    modifier    : Modifier = Modifier,
    selected    : Boolean = false,
    actualDate  : Boolean = false,
    text        : String = "Mo",
    click       : () -> Unit = {}
) {

    var select by remember {
        mutableStateOf(selected)
    }

    val color by animateColorAsState(
        targetValue =   if (select) MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        else if(actualDate) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "Color button week"
    )

    val borderColor by animateColorAsState(
        targetValue =   if (select) MaterialTheme.colorScheme.outline else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "Color button week"
    )

    LaunchedEffect(selected) {
        select = selected
    }

    Box(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .aspectRatio(1f)
            .clickable {
                click()
            }
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = text,
            fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
    }

}

@Composable
fun ButtonDescriptionAndSubDescription(
    modifier    : Modifier = Modifier,
    description : String = "",
    sub         : String = "",
    image       : Int = R.drawable.pizza,
    click : () -> Unit = {}
) {

    Box(
        modifier    = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable() {
                click()
            }
    ) {

        Column(modifier = Modifier
            .padding(10.dp)) {
            
            Row() {
                Text(
                    modifier = Modifier.weight(1f),
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Image(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 2.dp)
                        .align(Alignment.CenterVertically)
                        .size(28.dp),
                    painter = painterResource(image),
                    contentDescription = "Adding button")
            }
            


            Text(
                modifier = Modifier,
                text = sub,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Preview
@Composable
private fun MainLightPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = {
            MainCore()
        }
    )
}

@Preview
@Composable
private fun MainDarkPreview() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = {
            MainCore()
        }
    )
}

@Preview
@Composable
private fun MainLightPreviewEmpty() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = {
            MainCore(state = MainState(
                state = MainStatus.EMPTY
            ))
        }
    )
}

@Preview
@Composable
private fun MainDarkPreviewEmpty() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = {
            MainCore(state = MainState(
                state = MainStatus.EMPTY
            ))
        }
    )
}