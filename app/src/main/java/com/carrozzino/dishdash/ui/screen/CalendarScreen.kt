package com.carrozzino.dishdash.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.data.repository.models.RecipeDayModel
import com.carrozzino.dishdash.data.repository.models.RecipeModel
import com.carrozzino.dishdash.ui.theme.BlackOpacity
import com.carrozzino.dishdash.ui.theme.White90
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun CalendarScreen(
    viewModel: MainViewModel = hiltViewModel<MainViewModel>(),
    navigate : (Int) -> Unit = {}
) {

    val state = viewModel.mainState.collectAsState().value
    CalendarCore(state = state) {
        navigate(it)
    }
}

@Composable
fun CalendarCore(
    state : MainState = MainState(),
    navigate : (Int) -> Unit = {}
) {
    val coroutine = rememberCoroutineScope()

    LazyColumn(modifier = Modifier
        .fillMaxSize()) {
        item {

            MainTitle(
                modifier = Modifier,
                title = "Dish Dash"
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(modifier = Modifier.padding(horizontal = 25.dp).fillMaxWidth()) {
                NewActionButton(
                    imageVector = Icons.Rounded.Add,
                    background = MaterialTheme.colorScheme.primary
                ) {
                    navigate(0)
                }

                Spacer(modifier = Modifier.weight(1f))

                NewActionButton(
                    imageVector = Icons.Rounded.Refresh,
                    background = MaterialTheme.colorScheme.primary
                ) {
                    navigate(2)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Text(
                modifier = Modifier
                    .padding(top = 6.dp, start = 25.dp, end = 15.dp),
                text = "This week's menu:",
                style = MaterialTheme.typography.titleSmall
            )
        }

        itemsIndexed(state.recipes) { index, recipe ->
            SingleDay(
                recipeDayModel = recipe,
                selected = state.actualDate == recipe.date,
                coroutine = coroutine
            )
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

@Composable
fun SingleDay(
    recipeDayModel: RecipeDayModel,
    selected : Boolean = false,
    coroutine : CoroutineScope = rememberCoroutineScope()) {

    var expanded by remember { mutableStateOf(selected) }

    val paddingScaling by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(durationMillis = 100),
        label = "paddingScaleAnimation"
    )

    ElevatedCard(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(
                horizontal = (((1 - paddingScaling) * 10) + 16).dp,
                vertical = ((paddingScaling * 10) + 8).dp
            )
            .clickable {
                expanded = !expanded
            },
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = (paddingScaling * 4 + 1).dp),
    ) {

        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(tween(100)) togetherWith fadeOut(tween(100))
            },
            label = "ContentSwitch"
        ) { targetExpanded ->

            if(targetExpanded) {
                SingleDayExpanded(
                    modifier = Modifier,
                    url = recipeDayModel.recipeModel.urlImage,
                    main = recipeDayModel.recipeModel.main,
                    side = recipeDayModel.recipeModel.side,
                    mainIngredients = recipeDayModel.recipeModel.mainIngredients,
                    sideIngredients = recipeDayModel.recipeModel.sideIngredients,
                    date = recipeDayModel.date,
                    coroutine = coroutine,
                    )
            } else {
                SingleDayNotExpanded(
                    modifier = Modifier,
                    url = recipeDayModel.recipeModel.urlImage,
                    main = recipeDayModel.recipeModel.main,
                    side = recipeDayModel.recipeModel.side,
                    mainIngredients = recipeDayModel.recipeModel.mainIngredients,
                    sideIngredients = recipeDayModel.recipeModel.sideIngredients,
                    date = recipeDayModel.date,
                )
            }
        }
    }
}

@Composable
fun SingleDayExpanded(
    modifier : Modifier = Modifier,
    url : String = "",
    main : String = "",
    mainIngredients : String = "",
    side : String = "",
    sideIngredients : String = "",
    date : String = "",
    coroutine : CoroutineScope = rememberCoroutineScope()
) {

    var background by remember {
        mutableStateOf(BlackOpacity)
    }

    Box(modifier = modifier
        .fillMaxWidth()
        .height(240.dp)) {

        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .allowHardware(false)
                .crossfade(true)
                .build(),
            contentDescription = "Image with fade",
            placeholder = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop,
            onSuccess = { success ->
                coroutine.launch {
                    val bitmap = success.result.image.toBitmap()
                    val palette = Palette.from(bitmap).generate()

                    palette.darkVibrantSwatch?.let {
                        background = Color(
                            red = it.rgb.red,
                            green = it.rgb.green,
                            blue = it.rgb.blue,
                            alpha = it.rgb.alpha / 2)
                    }
                }
            }
        )

        Text(
            modifier = Modifier.padding(10.dp),
            text = date,
            style = MaterialTheme.typography.titleSmall,
            color = White90
        )

        Box (modifier = Modifier
            .padding(5.dp)
            .align(Alignment.TopEnd)
            .clip(RoundedCornerShape(10.dp))
            .shadow(3.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                // TODO
            }) {
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .size(18.dp),
                imageVector = Icons.Rounded.Create,
                tint = MaterialTheme.colorScheme.background,
                contentDescription = ""
            )
        }

        Box(modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = main,
                    style = MaterialTheme.typography.titleMedium,
                    color = White90
                )
                Text(
                    modifier = Modifier,
                    text = mainIngredients,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White90
                )

                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = side,
                    style = MaterialTheme.typography.titleMedium,
                    color = White90
                )
                Text(
                    modifier = Modifier,
                    text = sideIngredients,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = White90
                )
            }
        }
    }
}

@Composable
fun SingleDayNotExpanded(
    modifier : Modifier = Modifier,
    url : String = "",
    main : String = "",
    mainIngredients : String = "",
    side : String = "",
    sideIngredients : String = "",
    date : String = "",
) {

    Column(modifier = modifier) {

        Text(
            modifier = Modifier
                .padding(top = 6.dp, start = 6.dp, end = 6.dp),
            text = date,
            style = MaterialTheme.typography.titleSmall
        )

        Row(modifier = Modifier) {
            AsyncImage(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(10.dp))
                    .size(90.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Image with fade",
                placeholder = painterResource(R.drawable.hamburger),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = main,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier,
                    text = mainIngredients,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    modifier = Modifier.padding(end = 6.dp),
                    text = side,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier,
                    text = sideIngredients,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPreview() {
    val state = MainState(
        recipes = listOf<RecipeDayModel>(
            RecipeDayModel(
                recipeModel = RecipeModel(
                    main = "Daniele",
                    side = "Carrozzino"
                ),
                date = "Monday today"
            ),
            RecipeDayModel(
                recipeModel = RecipeModel(
                    main = "Daniele",
                    side = "Carrozzino"
                ),
                date = "Monday tomorrow"
            ),
            RecipeDayModel(
                recipeModel = RecipeModel(
                    main = "Daniele",
                    side = "Carrozzino"
                ),
                date = "Monday what"
            )),
        actualDate = "Monday today"
    )
    CalendarCore(state = state)
}

@Preview(showBackground = true)
@Composable
fun SingleDayNotExpandedPreview() {
    SingleDayNotExpanded(
        url = "https://images.immediate.co.uk/production/volatile/sites/30/2014/05/Epic-summer-salad-hub-2646e6e.jpg?quality=90&webp=true&resize=300,272",
        main = "default",
        mainIngredients = "default",
        side = "default",
        sideIngredients = "default"
    )
}