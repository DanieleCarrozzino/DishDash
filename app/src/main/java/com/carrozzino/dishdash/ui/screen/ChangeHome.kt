package com.carrozzino.dishdash.ui.screen

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
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.carrozzino.dishdash.ui.screen.settings.TitleAndBackButton
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.theme.Red50
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainViewModel
import com.carrozzino.dishdash.ui.viewModels.UserIntent
import kotlinx.coroutines.launch

@Composable
fun ChangeHomeScreen (
    navController   : NavController,
    viewmodel       : MainViewModel,
    modifier        : Modifier,
) {
    val state = viewmodel.mainState.collectAsStateWithLifecycle().value
    ChangeHomeCore(
        modifier = modifier,
        navController = navController,
        state = state,
        event = viewmodel::onReceive
    )
}

@Composable
fun ChangeHomeCore (
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
        Column(modifier = modifier.fillMaxSize()) {

            TitleAndBackButton(
                title = "Handle\nyour space!"
            ) { navController.navigateUp() }

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                // Insert a new code
                InsertNewCode(modifier = Modifier.align(Alignment.Center)) {
                    if(it.length > 4)
                        event(UserIntent.OnUpdatingNewCode(it))
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 30.dp)
                    .background(MaterialTheme.colorScheme.surface)
            )

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                // Share your code
                ShareYourCode(
                    modifier = Modifier.align(Alignment.Center),
                    code = state.code
                )
            }
        }
    }
}

@Composable
fun ShareYourCode(
    modifier : Modifier = Modifier,
    code : String = ""
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(20.dp)
    ) {
        Row(modifier = Modifier
            .padding(bottom = 20.dp)
            .align(Alignment.CenterHorizontally)) {

            Column(modifier = Modifier
                .weight(2f)
                .align(Alignment.Bottom)
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {

                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "Invite a new Guest!",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "Share this code to welcome a new member into your house!",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground
                )

            }



            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                painter = painterResource(R.drawable.sending_code),
                contentDescription = "Welcome home"
            )
        }


        Row(modifier = Modifier
            .padding(5.dp)
            .align(Alignment.CenterHorizontally)) {

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
                    text = code)
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
                        }}
            ) {
                Icon(
                    modifier = Modifier.size(22.dp).align(Alignment.Center),
                    imageVector = Icons.Rounded.CopyAll,
                    contentDescription =  "",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun InsertNewCode(
    modifier : Modifier = Modifier,
    send : (String) -> Unit = {}
) {
    var code by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(20.dp)
    ) {
        Row(modifier = Modifier
            .padding(bottom = 20.dp)
            .align(Alignment.CenterHorizontally)) {

            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                painter = painterResource(R.drawable.welcome_home),
                contentDescription = "Welcome home"
            )

            Column(modifier = Modifier
                .padding(bottom = 10.dp)
                .weight(2f)
                .align(Alignment.Bottom)
                .padding(start = 10.dp, end = 10.dp)) {

                Text(
                    modifier = Modifier,
                    text = "Be a new Guest!",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier,
                    text = "Got a friend's code?\nEnter it here to join their world!",
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        
        Row(modifier = Modifier
            .padding(5.dp)
            .align(Alignment.CenterHorizontally)) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 5.dp
            ) {
                TextField(
                    modifier = Modifier.padding(3.dp),
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(text = "code") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        disabledTextColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        errorContainerColor = MaterialTheme.colorScheme.surface,
                        errorLabelColor = Red50,
                        errorTextColor = Red50
                    ),
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Person,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = ""
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp)
                    .size(62.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {send(code)}
            ) {
                Icon(
                    modifier = Modifier.size(22.dp).align(Alignment.Center),
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription =  "",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview()
@Composable
fun ListScreenPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            ChangeHomeCore(
                state = MainState()
            )
        }
    )
}

@Preview()
@Composable
fun ListScreenPreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            ChangeHomeCore(
                state = MainState()
            )
        }
    )
}