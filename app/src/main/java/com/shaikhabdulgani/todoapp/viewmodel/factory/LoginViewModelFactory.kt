package com.shaikhabdulgani.todoapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shaikhabdulgani.todoapp.repo.UserRepo
import com.shaikhabdulgani.todoapp.viewmodel.LoginViewModel

class LoginViewModelFactory(private val repo: UserRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repo) as T
    }
}