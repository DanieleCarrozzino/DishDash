package com.carrozzino.dishdash.ui.screen.settings

import android.content.ClipData
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.launch

@Composable
fun SharePersonalCodeScreen (
    navController   : NavController,
    viewmodel       : MainViewModel,
    modifier        : Modifier,
) {
    val state = viewmodel.mainState.collectAsStateWithLifecycle().value
    SharePersonalCodeCore(
        modifier = modifier,
        navController = navController,
        state = state,
        event = viewmodel::onReceive
    )
}

@Composable
fun SharePersonalCodeCore (
    modifier        : Modifier      = Modifier,
    navController   : NavController = rememberNavController(),
    state           : MainState     = MainState(),
    event           : (UserIntent) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Absorb clicks */ }
    ) {
        ShareYourCode(
            navController = navController,
            code = state.personalCode
        )
    }
}

@Composable
fun ShareYourCode(
    navController: NavController = rememberNavController(),
    code : String = ""
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            TitleAndBackButton(
                title = ""
            ) { navController.navigateUp() }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(20.dp)
            ) {

                Image(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(0.7f)
                        .align(Alignment.CenterHorizontally),
                    painter = painterResource(R.drawable.sending_code),
                    contentDescription = "Welcome home"
                )

                Text(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .align(Alignment.CenterHorizontally),
                    text = "Invite a new Guest!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Share this code to welcome\na new member into your house!",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )


                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                        .align(Alignment.CenterHorizontally)
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp)
                            .height(62.dp)
                            .shadow(elevation = 5.dp, shape = RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 14.dp),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = code
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(62.dp)
                            .shadow(elevation = 5.dp, shape = RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                scope.launch {
                                    val clipData = ClipData.newPlainText("plain text", code)
                                    val clipEntry = ClipEntry(clipData)
                                    clipboardManager.setClipEntry(clipEntry)
                                    snackbarHostState.showSnackbar("Copied to clipboard!")
                                }
                            }
                    ) {
                        Icon(
                            modifier = Modifier.size(22.dp).align(Alignment.Center),
                            imageVector = Icons.Rounded.CopyAll,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
fun SharePersonalCodePreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            SharePersonalCodeCore(
                state = MainState()
            )
        }
    )
}

@Preview()
@Composable
fun SharePersonalCodePreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            SharePersonalCodeCore(
                state = MainState()
            )
        }
    )
}