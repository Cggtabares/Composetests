package com.example.composetests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetests.LoginScreens.CreateAccountScreen
import com.example.composetests.LoginScreens.MainViewModel
import com.example.composetests.ui.theme.ComposetestsTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposetestsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    CreateAccountScreen(MainViewModel())
                }
            }
        }
    }
}

@Composable
fun MyGoogleMaps(){
    val location = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 10f)
        //val uisettings /= remember {
            //MapUiSettings(myLocationButtonEnabled = true)
        //}
       // val properties by remember {
         //   mutableStateOf((ismylocationenabled = true))
        //}
    }
    Box(modifier = Modifier
        .size(width = 300.dp, height = 200.dp)
        .padding(10.dp)){
    /*GoogleMap(
        modifier = Modifier,
        cameraPositionState = cameraPositionState
    ){
        Marker(
            state = MarkerState(position = location),
            title = "Singapore")
    }*/

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            //uiSettings = uisettings,
        ) {

        }
    }}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposetestsTheme {
        MyGoogleMaps()
    }
}