package com.carrozzino.dishdash.data.network.authentication

import android.app.Activity

interface FirebaseAuthenticationInterface {

    fun authWithCredentials(email: String, password: String, callback : (Int) -> Unit )

    suspend fun signInWithGoogle(activity : Activity, callback: (Int) -> Unit)

    fun getUser() : String = ""

}