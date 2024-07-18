package com.shaikhabdulgani.todoapp.repo

import com.shaikhabdulgani.todoapp.data.model.User
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.util.Result

class UserRepo(private val db: AppDatabase) {

    companion object {
        private const val USERNAME = "admin"
        private const val PASSWORD = "admin"
    }

    fun login(username: String, password: String): Result<User> {
        if (username == USERNAME && password == PASSWORD) {
            return Result.Success(User(username, password))
        }
        return Result.Error("Username and Password doesn't match")
    }
}