package com.shaikhabdulgani.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.util.GeneralEvent
import com.shaikhabdulgani.todoapp.util.Result
import com.shaikhabdulgani.todoapp.util.TodoInputEvent
import com.shaikhabdulgani.todoapp.util.TodoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaveTodoViewModel(private val repo: TodoRepo) : ViewModel() {

    private var _todoState: MutableLiveData<TodoState> = MutableLiveData(TodoState())
    val todoState: LiveData<TodoState>
        get() = _todoState

    private var _generalEvent = MutableLiveData<GeneralEvent>(GeneralEvent.None)
    val generalEvent: LiveData<GeneralEvent>
        get() = _generalEvent


    fun onEvent(event: TodoInputEvent) {
        when (event) {
            TodoInputEvent.Submit -> saveTodo()
            is TodoInputEvent.SubtitleChange -> onSubtitleChange(event.subtitle)
            is TodoInputEvent.TitleChange -> onTitleChange(event.title)
            is TodoInputEvent.InitTodo -> onInitTodo(event.id)
            is TodoInputEvent.Update -> saveTodo()
        }
    }

    private fun onInitTodo(id: Long) {
        if (_todoState.value?.id != id) {
            viewModelScope.launch(Dispatchers.IO) {
                val todo = repo.getById(id)
                withContext(Dispatchers.Main){
                    _todoState.value = _todoState.value?.copy(
                        id = todo.id,
                        title = todo.title,
                        subtitle = todo.subtitle,
                        isCompleted = todo.isCompleted,
                    )
                }
            }
        }
    }

    private fun onSubtitleChange(subtitle: String) {
        val error = if (subtitle.isBlank()) "Subtitle cannot be empty" else ""
        _todoState.value = _todoState.value?.copy(
            subtitle = subtitle,
            subtitleError = error,
        )
    }

    private fun onTitleChange(title: String) {
        val error = if (title.isBlank()) "Title cannot be empty" else ""
        if (title==_todoState.value?.title){
            _todoState.value = _todoState.value?.copy(
                titleError = error,
            )
            return
        }
        _todoState.value = _todoState.value?.copy(
            title = title,
            titleError = error,
        )
    }

    private fun saveTodo() {
        val todoData = _todoState.value ?: return

        if (todoData.title.isBlank() ||
            todoData.subtitle.isBlank() ||
            todoData.titleError.isNotBlank() ||
            todoData.subtitleError.isNotBlank()
        ) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = repo.save(
                Todo(
                    id = todoData.id,
                    title = todoData.title,
                    subtitle = todoData.subtitle,
                    isCompleted = todoData.isCompleted,
                    timestamp = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main){
                when (res) {
                    is Result.Error -> _generalEvent.value = GeneralEvent.Failed(res.message)
                    is Result.Success -> _generalEvent.value = GeneralEvent.Success("Todo Saved")
                }
            }
        }

    }
}