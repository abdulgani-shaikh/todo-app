package com.shaikhabdulgani.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val todoRepo: TodoRepo) : ViewModel() {

    private val _todo = MutableLiveData<List<Todo>>(emptyList())
    val todo: LiveData<List<Todo>>
        get() = _todo

    fun getAllTodo(start: Long = 0, end: Long = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.getAllTodo().asFlow().map {
                if (start == 0L || end == 0L) {
                    it
                } else {
                    it.filter { todo: Todo -> todo.timestamp in start..end }
                }
            }.collect {
                withContext(Dispatchers.Main){
                    _todo.value = it
                }
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.delete(todo)
        }
    }

    fun setComplete(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.save(todo.copy(isCompleted = true))
        }
    }
}