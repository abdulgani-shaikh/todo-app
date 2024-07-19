package com.shaikhabdulgani.todoapp.repo

import android.util.Log
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.util.Result


class TodoRepo(private val db: AppDatabase) {

    companion object{
        private const val TAG = "TodoRepo"
    }
    suspend fun getAllTodo() = db.todoDao.getAllTodo()

    suspend fun getById(id: Long) = db.todoDao.getById(id)

    suspend fun save(todo: Todo): Result<Todo> {
        val result = db.todoDao.save(todo)
        Log.d(TAG,"$result")
        return Result.Success(todo)
    }

    suspend fun delete(todo: Todo): Result<String> {
        db.todoDao.delete(todo)
        return Result.Success("Success deleting todo")
    }
}