package com.shaikhabdulgani.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.repo.TodoRepo

class HomeViewModel(private val todoRepo: TodoRepo) : ViewModel() {
    fun getAllTodo() = todoRepo.getAllTodo()

    fun deleteTodo(todo: Todo) {
        todoRepo.delete(todo)
    }

    fun setComplete(todo: Todo) {
        todoRepo.save(todo.copy(isCompleted = true))
    }
}