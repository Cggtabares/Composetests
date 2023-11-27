package com.example.composetests.LoginScreens

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetests.Model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


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

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _isCreateEnable = MutableLiveData<Boolean>()
    val isCreateEnable: LiveData<Boolean> = _isCreateEnable


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
        _isCreateEnable.value =enableCreateAccountButton(name, email) //falta type, password, phone, lastname

    }

    fun enableCreateAccountButton(
        email: String,
        password: String,
       /* name: String,
        lastname: String,
        phone: String,*/
        //type: String,

        ) = Patterns.EMAIL_ADDRESS.matcher(email)
            .matches() && password.length >= 8
                // &&
               //name.isNotEmpty() &&
                //lastname.isNotEmpty() &&
                //phone.length == 10
                //&&                type.isNotEmpty()*/

    //return _email.value?.isNotEmpty() == true && _password.value?.isNotEmpty() == true

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
            name = name.toString(),
            lastName = lastname.toString(),
            email = email.toString(),
            phone = 123456789,
            type = "Pacientes",
            location = null,
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


    /*
        fun createNewUser(user: User) {



        }*/

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


}
