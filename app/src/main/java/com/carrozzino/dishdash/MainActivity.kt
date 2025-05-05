package com.carrozzino.dishdash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.carrozzino.dishdash.data.internal.Preferences
import com.carrozzino.dishdash.data.network.authentication.FirebaseAuthenticationInterface
import com.carrozzino.dishdash.ui.navigation.Screen
import com.carrozzino.dishdash.ui.navigation.SetupNavGraph
import com.carrozzino.dishdash.ui.theme.DishDashTheme
import com.carrozzino.dishdash.ui.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var navController  : NavHostController
    private val loginViewModel : Lazy<LoginViewModel> = viewModels<LoginViewModel>()
    @Inject lateinit var preferences : Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // if not logged in
        var startingRoute : Screen = Screen.Login
        if(!preferences.isLogged()) {
            loginViewModel.value.loginFromActivity = ::login
        } else startingRoute = Screen.Calendar

        setContent {
            navController = rememberNavController()

            DishDashTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CompositionLocalProvider(LocalInnerPadding provides innerPadding) {
                        SetupNavGraph(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            start = startingRoute,
                            login = loginViewModel.value
                        )
                    }
                }
            }
        }
    }

    private suspend fun login(firebaseAuth : FirebaseAuthenticationInterface, callback : (Int) -> Unit = {}) {
        println("$TAG::login")
        firebaseAuth.signInWithGoogle(this, callback)
    }
}

val LocalInnerPadding = compositionLocalOf { PaddingValues() }

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DishDashTheme {
        Greeting("Android")
    }
}