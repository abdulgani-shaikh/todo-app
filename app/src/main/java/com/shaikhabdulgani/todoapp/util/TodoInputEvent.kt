package com.shaikhabdulgani.todoapp.util

import com.shaikhabdulgani.todoapp.data.model.Todo

sealed interface TodoInputEvent {
    data class InitTodo(val id: Long) : TodoInputEvent
    data class TitleChange(val title: String) : TodoInputEvent
    data class SubtitleChange(val subtitle: String) : TodoInputEvent
    data object Submit : TodoInputEvent
    data class Update(val todo: Todo) : TodoInputEvent
}