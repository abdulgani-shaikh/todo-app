package com.shaikhabdulgani.todoapp.util

sealed class GeneralEvent(val message: String? = null) {

    data object None : GeneralEvent()
    data object Loading : GeneralEvent()
    class Success(message: String?) : GeneralEvent(message)
    class Failed(error: String?) : GeneralEvent(error)

}