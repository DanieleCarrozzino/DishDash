package com.carrozzino.dishdash.ui.screen.settings

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import com.carrozzino.dishdash.ui.viewModels.UserIntent

@Composable
fun AboutScreen (
    navController   : NavController,
    viewmodel       : MainViewModel,
    modifier        : Modifier,
) {
    val state = viewmodel.mainState.collectAsStateWithLifecycle().value
    AboutCore(
        modifier = modifier,
        navController = navController,
        state = state,
        event = viewmodel::onReceive
    )
}

@Composable
fun AboutCore (
    modifier        : Modifier      = Modifier,
    navController   : NavController = rememberNavController(),
    state           : MainState     = MainState(),
    event           : (UserIntent) -> Unit = {}
) {

    val context         = LocalContext.current
    val packageName     = context.packageName
    val packageManager  = context.packageManager
    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(packageName, 0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Absorb clicks */ }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            TitleAndBackButton(
                title = ""
            ) { navController.navigateUp() }

            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 15.dp),
                    text        = "Designed and Produced by",
                    color       = MaterialTheme.colorScheme.onBackground,
                    style       = MaterialTheme.typography.titleMedium,
                    textAlign   = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 15.dp),
                    text        = "Ilaria Falbo and Daniele Carrozzino",
                    color       = MaterialTheme.colorScheme.onBackground,
                    style       = MaterialTheme.typography.titleLarge,
                    textAlign   = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 15.dp),
                    text        = "aka Lilìn and Carrozzén",
                    color       = MaterialTheme.colorScheme.onBackground,
                    style       = MaterialTheme.typography.titleMedium,
                    textAlign   = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 15.dp)
                        .alpha(0.6f),
                    text        = packageInfo.versionName.toString(),
                    color       = MaterialTheme.colorScheme.onBackground,
                    style       = MaterialTheme.typography.titleMedium,
                    textAlign   = TextAlign.Center
                )
            }

        }

        Image(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.7f),
            painter = painterResource(R.drawable.me),
            contentDescription = "Personal image"
        )
    }
}

@Preview()
@Composable
fun AboutPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            AboutCore(
                state = MainState()
            )
        }
    )
}

@Preview()
@Composable
fun AboutPreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            AboutCore(
                state = MainState()
            )
        }
    )
}