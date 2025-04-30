package com.carrozzino.dishdash.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carrozzino.dishdash.ui.screen.AddingScreen
import com.carrozzino.dishdash.ui.screen.AddingScreenPresentation
import com.carrozzino.dishdash.ui.screen.LoginScreen
import com.carrozzino.dishdash.ui.screen.MainScreen
import com.carrozzino.dishdash.ui.viewModels.LoginViewModel
import com.carrozzino.dishdash.ui.viewModels.MainViewModel

sealed class Screen(val route : String) {
    data object Home : Screen("Home")
    data object Login : Screen("Login")
    data object Adding : Screen("Adding")
}

@Composable
fun SetupNavGraph(
    modifier        : Modifier,
    navController   : NavHostController,
    login           : LoginViewModel,
    main            : MainViewModel = hiltViewModel<MainViewModel>(),
    start           : Screen
){
    NavHost(
        navController = navController,
        startDestination = start.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            MainScreen(
                navController = navController,
                modifier = modifier,
                viewModel = main,
            )
        }
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(
                modifier = modifier,
                navController = navController,
                viewmodel = login
            )
        }

        composable(
            route = Screen.Adding.route
        ) {
            AddingScreen(
                modifier = modifier,
                navController = navController,
                viewModel = main
            )
        }
    }
}