package com.shaikhabdulgani.todoapp.util

data class LoginState(
    var username: String = "",
    var password: String = "",
    var usernameError: String = "",
    var passwordError: String = "",
)
