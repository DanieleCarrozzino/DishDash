package com.carrozzino.dishdash.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.DarkBlue
import com.carrozzino.dishdash.ui.viewModels.MainViewModel

@Composable
fun MainScreen(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>()
) {
    MainCore(
        modifier = modifier,
        navController = navController,
        viewModel = viewModel)
}

@Composable
fun MainCore(
    modifier: Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewModel : MainViewModel = hiltViewModel<MainViewModel>()
    ) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {

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