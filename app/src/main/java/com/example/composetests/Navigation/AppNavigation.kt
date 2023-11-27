package com.example.composetests.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetests.LoginScreens.CreateAccountScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = AppScreens.CreateUserScreen.name
    ){
        composable(AppScreens.CreateUserScreen.name){
            //CreateAccountScreen(navController = navController)
        }
        composable(AppScreens.LoginScreen.name){
            //AppLoginScreen(navController = navController)
        }

    }
}