package com.carrozzino.dishdash.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.R
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.theme.DarkColorScheme
import com.carrozzino.dishdash.ui.theme.LightColorScheme
import com.carrozzino.dishdash.ui.theme.Red50
import com.carrozzino.dishdash.ui.theme.Typography
import com.carrozzino.dishdash.ui.viewModels.LoginState
import com.carrozzino.dishdash.ui.viewModels.LoginViewModel

@Composable
fun LoginScreen(
    modifier : Modifier = Modifier,
    navController : NavController = rememberNavController(),
    viewmodel : LoginViewModel = hiltViewModel<LoginViewModel>(),
) {
    val loginState = viewmodel.loginState.collectAsState()
    var startOnce by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = loginState.value, key2 = startOnce) {
        if(loginState.value.isLogged && !startOnce) {
            startOnce = true
            navController.navigate(Screen.Home.route){ launchSingleTop = true }
        }
    }

    LoginCore(
        modifier = modifier,
        loginState = loginState.value
    ) { type, username, password ->
        if(type == 0)
            viewmodel.login(username, password)
        else if(type == 1)
            viewmodel.loginWithGoogle()
    }
}

@Composable
private fun LoginCore(
    modifier : Modifier = Modifier,
    loginState : LoginState = LoginState(),
    login : (Int, String, String) -> Unit = {_, _, _ ->}
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {

        LoadingVegetables(modifier = Modifier.blur(20.dp))
        
        Column(modifier = modifier.align(Alignment.Center)) {

            Box(modifier = Modifier.weight(1f).fillMaxSize()) {

                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = "Dish Dash",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp,
                        lineHeight = 20.sp
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = "Needs ideas? why not",
                        fontSize = 26.sp,
                        lineHeight = 16.sp
                    )
                }
            }



            Credentials(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(2f),
                username = loginState.username,
                password = loginState.password,
                error = loginState.error
            ) { type, username, password ->
                login(type, username, password)
            }

            Box(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Credentials(
    modifier : Modifier = Modifier,
    username : String = "Daniele",
    password : String = "Password",
    error : Boolean = false,
    login : (Int, String, String) -> Unit = {_, _, _ ->}
) {

    var _username       by remember { mutableStateOf(username) }
    var _password       by remember { mutableStateOf(password) }
    var _error          by remember { mutableStateOf(error) }
    var hiddenPassword  by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = error) {
        println("LoginScreen::Credentials error values changed")
        _error = error
    }

    Column(modifier = modifier) {
        // Username
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 5.dp
        ) {
            TextField(
                modifier = Modifier.padding(3.dp),
                value = _username,
                isError = _error,
                onValueChange = {
                    _username = it
                    if(_error) _error = false },
                label = { Text(text = "username") },
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
                        tint = if(_error) Red50 else MaterialTheme.colorScheme.onBackground,
                        contentDescription = ""
                    )
                }
            )
        }

        // Password
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(10.dp, 4.dp, 10.dp, 10.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 5.dp
        ) {
            TextField(
                modifier = Modifier.padding(3.dp),
                value = _password,
                onValueChange = {
                    _password = it
                    if(_error) _error = false },
                isError = _error,
                label = { Text(text = "password") },
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
                visualTransformation = if (hiddenPassword)
                    PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val painter = if (hiddenPassword) R.drawable.baseline_visibility_24
                        else R.drawable.baseline_visibility_off_24
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                hiddenPassword = !hiddenPassword
                            },
                        painter = painterResource(id = painter),
                        tint = if(_error) Red50 else MaterialTheme.colorScheme.onBackground,
                        contentDescription = ""
                    )
                },
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Rounded.Lock,
                        tint = if(_error) Red50 else MaterialTheme.colorScheme.onBackground,
                        contentDescription = ""
                    )
                }
            )
        }

        ButtonLogin(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
            enabled = _username.isNotEmpty() && _password.isNotEmpty()
        ) {
            login(0, _username.trim(), _password.trim())
        }


        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp, 30.dp, 50.dp, 20.dp)){

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

            Text(text = "or",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp, 20.dp)
                    .background(MaterialTheme.colorScheme.background))
        }


        ButtonSignIn(
            modifier = Modifier
                .padding(4.dp)
                .background(Color.Transparent)
                .align(Alignment.CenterHorizontally),
            icon = R.drawable.google
        ) {
            login(1, "", "")
        }
    }
}

@Preview
@Composable
private fun LoginPreview() {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = {
            LoginCore()
        }
    )
}

@Preview
@Composable
private fun LoginDarkPreview() {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = {
            LoginCore()
        }
    )
}

@Composable
fun ButtonLogin(
    modifier : Modifier = Modifier,
    enabled : Boolean = true,
    click : () -> Unit = {},
){
    Card(
        modifier = modifier
            .alpha(if(enabled) 1f else 0.6f),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if(enabled) 4.dp else 0.dp),
        shape = RoundedCornerShape(30.dp)
    ) {

        Box(modifier = Modifier
            .clickable(enabled = enabled, onClick = click)
        ){
            Text(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 12.dp),
                text = "Login",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ButtonSignIn(
    modifier: Modifier,
    icon : Int = -1,
    signIn : () -> Unit) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(30.dp),
    ) {
        Row(modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .clickable { signIn() }
            .padding(35.dp, 4.dp, 35.dp, 4.dp)) {

            if(icon != -1) {
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = icon),
                    contentDescription = "icon")
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp),
                text = "Sign in",
                textAlign = TextAlign.Center)
        }
    }
}