package com.shaikhabdulgani.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaikhabdulgani.todoapp.repo.UserRepo
import com.shaikhabdulgani.todoapp.util.GeneralEvent
import com.shaikhabdulgani.todoapp.util.LoginState
import com.shaikhabdulgani.todoapp.util.UserInputEvent
import com.shaikhabdulgani.todoapp.util.Result

class LoginViewModel(private val repo: UserRepo) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private var _loginState = MutableLiveData(LoginState())
    val loginState: LiveData<LoginState>
        get() = _loginState

    private var _authEvent = MutableLiveData<GeneralEvent>(null)

    val authEvent: LiveData<GeneralEvent>
        get() = _authEvent


    fun onEvent(event: UserInputEvent) {
        when (event) {
            is UserInputEvent.UsernameChange -> onUsernameChange(event.username)
            is UserInputEvent.PasswordChange -> onPasswordChange(event.password)
            UserInputEvent.Submit -> login()
        }
    }

    private fun onPasswordChange(password: String) {
        if (password.isBlank()) {
            _loginState.value = _loginState.value?.copy(
                password = password,
                passwordError = "Password cannot be blank"
            )
            return
        }
        _loginState.value = _loginState.value?.copy(
            password = password,
            passwordError = ""
        )
    }

    private fun onUsernameChange(username: String) {
        if (username.isBlank()) {
            _loginState.value = _loginState.value?.copy(
                username = username,
                usernameError = "Username cannot be blank"
            )
            return
        }

        _loginState.value = _loginState.value?.copy(
            username = username,
            usernameError = ""
        )
    }

    private fun login() {
        val loginData = _loginState.value ?: return
        _authEvent.value = GeneralEvent.Loading

        if (
            loginData.username.isBlank() ||
            loginData.password.isBlank() ||
            loginData.usernameError.isNotBlank() ||
            loginData.passwordError.isNotBlank()
        ) {
            _authEvent.value = GeneralEvent.Failed("username or password cannot be blank")
            return
        }

        repo.login(loginData.username, loginData.password).also {
            when (it) {
                is Result.Error -> _authEvent.value = GeneralEvent.Failed(it.message)
                is Result.Success -> _authEvent.value = GeneralEvent.Success(it.message)

            }
        }
    }
}