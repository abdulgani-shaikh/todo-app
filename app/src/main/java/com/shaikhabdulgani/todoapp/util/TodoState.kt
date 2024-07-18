package com.shaikhabdulgani.todoapp.util

data class TodoState(
    val title: String = "",
    val subtitle: String = "",
    val titleError: String = "",
    val subtitleError: String = "",
    val id: Long = 0,
    val isCompleted: Boolean = false,
)