package com.carrozzino.dishdash.ui.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.theme.Red
import com.carrozzino.dishdash.ui.theme.settings1
import com.carrozzino.dishdash.ui.theme.settings4
import com.carrozzino.dishdash.ui.theme.settings5
import com.carrozzino.dishdash.ui.viewModels.MainState
import com.carrozzino.dishdash.ui.viewModels.MainViewModel


@Composable
fun SettingsScreen (
    navController   : NavController,
    viewmodel       : MainViewModel,
    modifier        : Modifier,
) {

    val state = viewmodel.mainState.collectAsStateWithLifecycle().value
    SettingsCore (
        modifier        = modifier,
        navController   = navController,
        state           = state
    ) {
        //viewmodel.logout()
    }
}

@Composable
fun SettingsCore (
    modifier        : Modifier      = Modifier,
    navController   : NavController = rememberNavController(),
    state           : MainState     = MainState(),
    logout          : () -> Unit    = {}
){
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Absorb clicks */ }
    )  {

        Box(modifier = modifier) {
            Column(modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 15.dp)
                .verticalScroll(rememberScrollState())) {

                // title and subtitle
                TitleAndBackButton(title = "Settings") {
                    navController.navigateUp()
                }

                // First block
                BoxInList {
                    Column {
                        SingleListItem(
                            title       = "Change home",
                            color       = MaterialTheme.colorScheme.onBackground,
                            iconColor   = settings1,
                            icon        = Icons.Rounded.Home
                        ) {
                            navController.navigate(Screen.ChangeHome.route) {
                                launchSingleTop = true
                            }
                        }

                        SingleListItem(
                            title       = "Share your code",
                            color       = MaterialTheme.colorScheme.onBackground,
                            iconColor   = settings5,
                            icon        = Icons.Rounded.Code
                        ) {
                            navController.navigate(Screen.ShareCode.route) {
                                launchSingleTop = true
                            }
                        }

//                        SingleListItem(
//                            title       = "Notifications and Sound",
//                            color       = MaterialTheme.colorScheme.onBackground,
//                            iconColor   = settings2,
//                            icon        = Icons.Rounded.Home
//                        ) {
////                            navController.navigate(Screen.Notification.route) {
////                                launchSingleTop = true
////                            }
//                        }
//
//                        SingleListItem(
//                            title       = "Microphone",
//                            color       = MaterialTheme.colorScheme.onBackground,
//                            iconColor   = settings3,
//                            icon        = Icons.Rounded.Home
//                        ) {
////                            navController.navigate(Screen.Microphone.route) {
////                                launchSingleTop = true
////                            }
//                        }
//
                        SingleListItem(
                            title           = "About",
                            color           = MaterialTheme.colorScheme.onBackground,
                            arrowVisible    = true,
                            iconColor       = settings4,
                            icon            = Icons.Outlined.Info
                        ) {
                            navController.navigate(Screen.About.route){
                                launchSingleTop = true
                            }
                        }
//
//                        SingleListItem(
//                            title           = "Advanced Settings",
//                            color           = MaterialTheme.colorScheme.onBackground,
//                            iconColor       = settings6,
//                            icon            = Icons.Rounded.Home
//                        ) {
////                            navController.navigate(Screen.Settings.route){
////                                launchSingleTop = true
////                            }
//                        }
                    }
                }

                // Logout block
                BoxInList {
                    SingleListItem(
                        title = "Logout",
                        color = Red,
                        badgeNumber = 0,
                        arrowVisible = false,
                        iconColor = Red,
                        icon = Icons.AutoMirrored.Rounded.ExitToApp
                    ) {
//                        logout()
//                        navController.navigate(Screen.Login.route) {
//                            launchSingleTop = true
//                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxInList(
    child : @Composable () -> Unit = @Composable{}
){
    Surface(
        modifier    = Modifier.padding(10.dp),
        shape       = RoundedCornerShape(12.dp),
        shadowElevation = 0.dp,
        color           = MaterialTheme.colorScheme.surface,
        content         = child,
        contentColor    = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun SingleListItem(
    title       : String        = "Default",
    color       : Color         = Color.Black,
    badgeNumber : Int           = 0,
    arrowVisible: Boolean       = true,
    iconColor   : Color         = Color.Red,
    icon        : ImageVector   = Icons.Rounded.Home,
    click       : () -> Unit    = {}
){
    Row(modifier = Modifier
        .clickable {
            click()
        }
        .padding(5.dp)
        .fillMaxWidth()) {

        Box(
            modifier = Modifier
                .padding(vertical = 3.dp, horizontal = 3.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                modifier = Modifier
                    .padding(9.dp)
                    .size(25.dp),
                imageVector = icon,
                contentDescription = "",
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .align(Alignment.CenterVertically)
                .weight(1f),
            text = title,
            fontWeight = FontWeight.SemiBold,
            color = color,
            lineHeight = 14.sp
        )

        if(badgeNumber > 0) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .align(Alignment.CenterVertically),
                text = badgeNumber.toString()
            )
        }

        if (arrowVisible) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .align(Alignment.CenterVertically)
                    .alpha(0.6f)
                    .size(25.dp),
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = ""
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
fun ListScreenPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = {
            SettingsCore(
                state = MainState()
            )
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
fun ListScreenPreviewDark() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = {
            SettingsCore(
                state = MainState()
            )
        }
    )
}

@Composable
fun TitleAndBackButton(
    modifier    : Modifier = Modifier,
    title       : String,
    close       : () -> Unit = {}
) {
    Row(modifier = modifier
        .padding(bottom = 22.dp, top = 10.dp)) {
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 15.dp),
            text        = title,
            color       = MaterialTheme.colorScheme.onBackground,
            style       = MaterialTheme.typography.titleLarge,
        )

        Surface(
            modifier = Modifier
                .padding(start = 10.dp, end = 15.dp)
                .size(45.dp)
                .align(Alignment.CenterVertically),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable { close() }) {
                Image(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Home Icon",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}