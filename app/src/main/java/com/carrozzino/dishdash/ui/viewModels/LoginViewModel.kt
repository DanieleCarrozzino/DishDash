package com.carrozzino.dishdash.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carrozzino.dishdash.data.internal.Preferences
import com.carrozzino.dishdash.data.network.authentication.FirebaseAuthenticationInterface
import com.carrozzino.dishdash.ui.utility.ViewModelUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState (
    val username : String = "",
    val password : String = "",
    val isLogged : Boolean = false,
    val error : Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val preferences : Preferences,
    private val firebaseAuth : FirebaseAuthenticationInterface
) : ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow(LoginState())
    val loginState : StateFlow<LoginState> = _loginState.asStateFlow()

    var loginFromActivity : suspend (FirebaseAuthenticationInterface, (Int) -> Unit) -> Unit = {_, _ ->}

    init {
        initData()
    }

    private fun initData() {
        _loginState.update {
            it.copy(
                username = preferences.getString("username"),
                password = preferences.getString("password"),
                isLogged = preferences.getBoolean("is_logged")
            )}
    }

    fun loginWithGoogle() {
        viewModelScope.launch(Dispatchers.IO) {
            loginFromActivity(firebaseAuth) {
                println("$TAG callback received after a google login | status : $it")
                loginResponse(it)
            }
        }
    }

    fun login(username : String, password : String) {
        preferences.putString(username, "username")
        preferences.putString(password, "password")

        _loginState.update { it.copy(error = false )}
        firebaseAuth.authWithCredentials(username, password) {
            println("$TAG callback received after a login | status : $it")
            loginResponse(it)
        }
    }

    private fun loginResponse(result : Int) {
        if(result == 0) {
            preferences.putBoolean(true, "is_logged")
            preferences.putString(ViewModelUtility.encodeToBase64(firebaseAuth.getUser()), "code")
            _loginState.update {
                it.copy(
                    isLogged = true,
                    error = false
                )}
        } else {
            _loginState.update {
                it.copy(
                    isLogged = false,
                    error = true
                )}
        }
    }
}