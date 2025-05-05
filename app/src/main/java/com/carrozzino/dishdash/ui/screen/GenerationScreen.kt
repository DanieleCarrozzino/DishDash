package com.carrozzino.dishdash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.viewModels.Intent
import com.carrozzino.dishdash.ui.viewModels.MainViewModel

@Composable
fun GenerationScreen(
    modifier : Modifier = Modifier,
    viewModel : MainViewModel = hiltViewModel<MainViewModel>(),
    navController : NavController = rememberNavController(),
    navigate : () -> Unit = {}
) {
    GenerationCore(
        modifier = modifier,
        click = viewModel::onReceive,
        navigate = navigate
    )
}

@Composable
fun GenerationCore(
    modifier : Modifier = Modifier,
    click : (intent : Intent) -> Unit = {},
    navigate : () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {

        Box(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    modifier = Modifier,
                    text = "Generate a New Weekly Menu!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    text = "Generate a brand new weekly recipe list. " +
                            "This action will replace your current week's recipes with a fresh selection",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LoadingVegetables(
            listOf(R.drawable.spaghetti, R.drawable.hamburger, R.drawable.burrito, R.drawable.meat)
        )

        Button(
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(10f),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp
            ),
            onClick = {
                click(Intent.OnGenerateNewWeek)
            }
        ) {
            Text(
                text = "Generate a new Week!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 18.dp)
                .align(Alignment.BottomStart)
                .zIndex(10f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            onClick = { navigate() }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "navigate back button",
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Go back\nto the Calendar",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun GenerationPreview() {
    GenerationCore()
}