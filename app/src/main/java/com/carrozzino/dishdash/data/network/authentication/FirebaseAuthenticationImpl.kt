package com.carrozzino.dishdash.data.network.authentication

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FirebaseAuthenticationImpl @Inject constructor(
    @ApplicationContext context : Context,
    private var firebaseAuthentication : FirebaseAuth,
    private val credentialManager: CredentialManager
) : FirebaseAuthenticationInterface {

    companion object {
        const val TAG = "FirebaseAuthenticationImpl"
    }

    override fun authWithCredentials(email: String, password: String, callback: (Int) -> Unit) {
        firebaseAuthentication.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // firebaseAuthentication.currentUser
                    callback(0)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        signInWithCredentials(email, password, callback)
                    } else callback(-1)
                }
            }
    }

    override suspend fun signInWithGoogle(activity : Activity, callback: (Int) -> Unit) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setServerClientId("744419240953-1vupp650msctrlsj1m203u2mvjdfeerb.apps.googleusercontent.com")
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = activity
            )

            val googleCredential = result.credential
            println(googleCredential)


            val idToken = googleCredential.type
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuthentication.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(0)
                } else {
                    callback(-1)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun signInWithCredentials(email: String, password: String, callback: (Int) -> Unit) {
        firebaseAuthentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuthentication.currentUser
                    callback(0)
                } else {
                    println("$TAG exception : ${task.exception}")
                    callback(-1)
                }
            }
    }

    override fun getUser(): String {
        return firebaseAuthentication.currentUser.toString()
    }
}