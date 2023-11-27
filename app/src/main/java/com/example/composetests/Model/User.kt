package com.example.composetests.Model

import android.location.Location

data class User(
    val uid: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: Int,
    val type: String,
    val location: String?,
    val cuidadorId: String?,

    ){

    fun toMap(): MutableMap<String, Any?>{
        return mutableMapOf(
            "uid" to this.uid,
            "name" to this.name,
            "lastName" to this.lastName,
            "email" to this.email,
            "phone" to this.phone,
            "type" to this.type,
            "location" to this.location,
            "cuidadorId" to this.cuidadorId
        )


    }





}