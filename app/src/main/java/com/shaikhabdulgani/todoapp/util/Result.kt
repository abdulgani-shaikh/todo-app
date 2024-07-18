package com.shaikhabdulgani.todoapp.util

sealed class Result<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(result: T?,message: String? = null) : Result<T>(result,message)
    class Error<T>(error: String, data: T? = null) : Result<T>(data, error)
}