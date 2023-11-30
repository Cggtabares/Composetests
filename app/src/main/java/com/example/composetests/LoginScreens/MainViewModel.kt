package com.example.composetests.LoginScreens

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetests.Model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {

    //Firebase Auth
    private val auth: FirebaseAuth = Firebase.auth

    //loading
    private val _loading = MutableLiveData(false)

    //Livedata de datos de usuario
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _lastname = MutableLiveData<String>()
    val lastname: LiveData<String> = _lastname

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?> = _currentLocation

    private val _isCreateEnable = MutableLiveData<Boolean>()
    val isCreateEnable: LiveData<Boolean> = _isCreateEnable

    private var permissionGranted = false

    fun onUpdateType(newType: String) {
        _type.value = newType
    }

    //Reflejar los cambios realizados en pantalla y habilitar el boton de crear cuenta, tambien puede servir para hacer login
    fun onCreateAccountChange(
        email: String,
        password: String,
        name: String,
        lastname: String,
        phone: String,
        type: String
    ) {
        _name.value = name
        _lastname.value = lastname
        _email.value = email
        _password.value = password
        _phone.value = phone
        _type.value = type
        _isCreateEnable.value =
            enableCreateAccountButton(email, password, name, lastname, phone, type)
    }

    fun enableCreateAccountButton(
        email: String,
        password: String,
        name: String,
        lastname: String,
        phone: String,
        type: String,

        ) = Patterns.EMAIL_ADDRESS.matcher(email)
        .matches()
            && validatePassword(password)
            && name.isNotEmpty()
            && lastname.isNotEmpty()
            && phone.length >= 10
            && type.isNotEmpty()

    //return _email.value?.isNotEmpty() == true && _password.value?.isNotEmpty() == true

    //Validacion de correo y password
    fun validateEmail(email: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
        return regex.matches(email)
    }

    fun validatePassword(password: String): Boolean {

        val showErrorMessage =
            "Se requiere al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial"
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&.])[A-Za-z\\d@$!%*#?&.]{8,}$")
        if (!regex.matches(password)) {
            Log.d("Login", "validatePassword: $showErrorMessage")

        }
        return regex.matches(password)

        //Password Validation
        //Minimum eight characters, at least one capital letter, at least one lower letter, one number and one special character:
        //"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    }


    fun createUser(
        email: String, password: String,
        //home: () -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val userId = auth.currentUser?.uid
                        /*val displayName =
                            task.result.user?.email?.split("@")?.get(0)*/
                        createUser(userId)
                        //home()
                    } else {
                        Log.d(
                            "LoginApp",
                            "createUserWithemailAndPassword: ${task.result.toString()}"
                        )
                    }
                    _loading.value = false
                }
        }

    }

    private fun createUser(userID: String?) {
        //Usando DataClass
        val user: MutableMap<String, Any?> = User(
            uid = userID.toString(),
            name = name.value.toString(),
            lastName = lastname.value.toString(),
            email = email.value.toString(),
            phone = phone.value.toString(),
            type = type.value.toString(),
            firstlocation = null,
            lastLocation = null,
            cuidadorId = null
        ).toMap()

        val collection = when (type.value) {
            "Paciente" -> "Pacientes"
            "Cuidador" -> "Cuidadores"
            else -> null
        }

        FirebaseFirestore.getInstance().collection(collection!!)
            .add(user)
            .addOnSuccessListener {
                Log.d("Crear", "Creado ${it.id}")

            }.addOnFailureListener {
                Log.d("Crear", "Error al crear usuario ${it}")
            }

    }

    //Ingresar a usuario creado
    fun signIn(email: String, password: String) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    Log.d(
                        "Login",
                        "singInWithEmailAndPassword Logueado!!!: ${authResult.toString()}"
                    )


                }
                .addOnFailureListener { ex ->
                    Log.d("Login", "singInWithEmailAndPassword Falló!!!: ${ex.localizedMessage}")
                }
        } catch (e: Exception) {
            Log.d("Login", "signInWithEmailAndPassword: ${e.message}")
        }

    }

    fun signOutAccount() {
        Firebase.auth.signOut()
        Log.d("LogOut", "Ha salido satisfactoriamente")
    }


    //Request Location functions




    /*
    //@SuppressLint("MissingPermission")
    fun getUserLocation(context: Context): Location? {
        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        val isUserLocationPermissionGranted = ContextCompat.checkSelfPermission( context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        if (!isGPSEnabled || !isUserLocationPermissionGranted) {
            return null
        }
        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        cont.resume(result) {}
                    } else {
                        cont.resume(null) {}
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener { it ->
                    cont.resume(it) {}
                }
                addOnFailureListener {
                    cont.resume(null) {}
                }
            }
        }

    }*/

}
