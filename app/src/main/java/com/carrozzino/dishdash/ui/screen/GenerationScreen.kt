package com.carrozzino.dishdash.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.utility.ViewModelUtility

@Composable
fun GenerationScreen(
    modifier : Modifier = Modifier,
    viewModel : MainViewModel = hiltViewModel<MainViewModel>(),
    navController : NavController = rememberNavController()
) {
    val state = viewModel.generatingState.collectAsState().value
    val coroutine = rememberCoroutineScope()

    var internal by remember { mutableIntStateOf(-1) }

    LaunchedEffect(key1 = state) {
        if(internal < 0 && state.generating) {
            internal = 0
        }
        else if(!state.generating) {
            delay(3000)
            internal = 1
            delay(7000)
            internal = if(state.error) 3 else 2
            delay(4000)
            coroutine.launch(Dispatchers.Main) {
                navController.popBackStack()
            }
        }
    }

    GenerationCore(
        modifier = modifier,
        state = internal)
}

@Composable
fun GeneratingImageAnimation(
    modifier : Modifier = Modifier,
    coroutine : CoroutineScope = rememberCoroutineScope(),
    error : Boolean = false
) {

    var id by remember { mutableIntStateOf((0..<ViewModelUtility.listImages.size).random()) }
    LaunchedEffect(key1 = Unit) {
        coroutine.launch {
            while(true) {
                delay(2000)
                id = (0..<ViewModelUtility.listImages.size).random()
            }
        }
    }

    AnimatedContent(
        targetState = id,
        transitionSpec = { slideInHorizontally { fullWidth -> fullWidth / 3 } + fadeIn() togetherWith
            slideOutHorizontally { fullWidth -> -fullWidth / 3 } + fadeOut()
        }
    ) { internal ->
        FoodAvatar(
            modifier = modifier,
            list = ViewModelUtility.listImages,
            id = internal
        )
    }


}

@Composable
fun GenerationCore(
    modifier : Modifier = Modifier,
    state : Int = 0) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {

        Column(modifier = modifier.align(Alignment.Center)) {

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Image(
                    modifier = Modifier.size(24.dp).padding(end = 5.dp),
                    painter = painterResource(R.drawable.carrot),
                    contentDescription = ""
                )

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "creating new week",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = when(state) {
                    0 -> "We’re picking the best recipes\njust for you... "
                    1 -> "Mixing flavors...\nWhat’s the perfect combination? ️"
                    2 -> "All set!\nYour delicious week is ready to enjoy!"
                    3 -> "Oops! Something went wrong on our side...\n(or maybe check your connection)"
                    else -> ""
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground)

            GeneratingImageAnimation(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

    }
}

@Preview
@Composable
fun GenerationPreview() {
    GenerationCore()
}