package com.carrozzino.dishdash.ui.screen

import android.R
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.ui.theme.Blue
import com.carrozzino.dishdash.ui.theme.DarkBlue
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import kotlinx.coroutines.launch

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
    val pagerState = rememberPagerState(
        pageCount = { 3 },
        initialPageOffsetFraction = 0f,
        initialPage = 1)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPageOffsetFraction }.collect { page ->
            Log.d("Page change", "Page changed to $page")
        }
    }

    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier
        .fillMaxSize()) {

        Box(modifier = modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState) { page ->
                println("Page loaded $page")
                when(page) {
                    0 -> { AddingScreenPresentation(
                        navController = navController,
                        viewModel = viewModel) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }}
                    1 -> { CalendarScreen(viewModel = viewModel) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    }}
                    2 -> { GenerationScreen(viewModel = viewModel) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }}
                }
            }
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