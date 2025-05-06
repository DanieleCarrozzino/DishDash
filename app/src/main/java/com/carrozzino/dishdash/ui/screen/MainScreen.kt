package com.carrozzino.dishdash.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.DarkBlue
import com.carrozzino.dishdash.ui.theme.White90
import com.carrozzino.dishdash.ui.viewModels.Intent
import com.carrozzino.dishdash.ui.viewModels.MainViewModel

@Composable
fun MainScreen(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>()
) {
    MainCore(
        modifier = modifier,
        navController = navController) {
        viewModel.onReceive(it)
    }
}

@Composable
fun MainCore(
    modifier: Modifier = Modifier,
    navController : NavController = rememberNavController(),
    click : (intent : Intent) -> Unit = {}
    ) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {

        LoadingVegetables(modifier = Modifier.blur(30.dp))

        Box(modifier = modifier.fillMaxSize()) {

            Column(modifier = Modifier.padding(vertical = 25.dp, horizontal = 20.dp)) {
                Row {
                    Image(
                        modifier = Modifier.size(24.dp).padding(end = 5.dp),
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

                Spacer(modifier = Modifier.height(20.dp))
                ButtonDescriptionAndSubDescription(
                    description = "Found a new recipe?",
                    sub = "Adding a new recipe to your virtual cook book!",
                    imageVector = Icons.Rounded.Add,
                ) {
                    navController.navigate(Screen.Adding.route)
                }

                Spacer(modifier = Modifier.height(20.dp))
                ButtonDescriptionAndSubDescription(
                    description = "Need a new week?",
                    sub = "Generate a new menu for your awesome week!",
                    imageVector = Icons.Rounded.Refresh,
                ) {
                    navController.navigate(Screen.Generate.route)
                    click(Intent.OnGenerateNewWeek)
                }
            }
        }
    }
}

@Composable
fun ButtonDescriptionAndSubDescription(
    modifier : Modifier = Modifier,
    description : String = "",
    sub : String = "",
    imageVector : ImageVector = Icons.Rounded.Add,
    click : () -> Unit = {}
) {
    Row(modifier = modifier
        .clip(RoundedCornerShape(10.dp))
        .clickable{click()}) {

        Column(modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(end = 18.dp).weight(1f)) {
            Text(
                modifier = Modifier,
                text = description,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                modifier = Modifier,
                text = sub,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable{click()}
        ) {
            Icon(
                modifier = Modifier.padding(20.dp),
                imageVector = imageVector,
                contentDescription = "Adding button",
                tint = White90)
        }
    }
}

@Composable
@Preview
fun MainPreview() {
    MainCore()
}

@Composable
fun MainTitle(
    modifier : Modifier = Modifier,
    title : String = ""
) {
    Text(
        modifier = modifier
            .padding(
                top = 14.dp,
                start = 25.dp),
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = DarkBlue
    )
}

@Preview(showBackground = true)
@Composable
fun MainTitlePreview() {
    MainTitle(title = "Title")
}