package com.carrozzino.dishdash.ui.screen.settings

import android.content.ClipData
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
        Box(modifier = modifier.fillMaxSize()) {
            TitleAndBackButton(
                title = ""
            ) { navController.navigateUp() }

            // Insert a new code
            InsertNewCode(modifier = Modifier.align(Alignment.Center)) {
                if(it.length > 4)
                    event(UserIntent.OnUpdatingNewCode(it))
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
        Image(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.7f)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.welcome_home),
            contentDescription = "Welcome home"
        )

        Text(
            modifier = Modifier
                .padding(top = 15.dp)
                .align(Alignment.CenterHorizontally),
            text = "Be a new Guest!",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Got a friend's code?\nEnter it here to join their world!",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        
        Row(modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 10.dp)
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
fun ChangeHomePreview() {
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
fun ChangeHomePreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            ChangeHomeCore(
                state = MainState()
            )
        }
    )
}