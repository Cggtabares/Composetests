package com.example.composetests.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.composetests.LoginScreens.MainViewModel
import com.example.composetests.Navigation.AppScreens

@Composable
fun Home(navController: NavController, viewModel: MainViewModel){
    Column {
        Text(text = "Estamos en Home!!!!")
        Button(
            onClick = { viewModel.signOutAccount().also {
                navController.navigate(AppScreens.LoginScreen.name) }
            }) {
            Text(text = "Cerrar Sesion")

        }
    }


}