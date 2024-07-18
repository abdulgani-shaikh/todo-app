package com.shaikhabdulgani.todoapp.util

sealed interface UserInputEvent {
    data object Submit : UserInputEvent
    data class UsernameChange(val username: String) : UserInputEvent
    data class PasswordChange(val password: String) : UserInputEvent
}
