package com.shaikhabdulgani.todoapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.shaikhabdulgani.todoapp.data.model.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_table ORDER BY isCompleted ASC,timestamp DESC")
    fun getAllTodo(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE id=:id")
    fun getById(id: Long): Todo

    @Upsert
    fun save(todo: Todo): Long

    @Delete
    fun delete(todo: Todo)
}