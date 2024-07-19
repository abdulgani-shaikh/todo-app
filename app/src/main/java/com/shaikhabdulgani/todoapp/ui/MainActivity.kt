package com.shaikhabdulgani.todoapp.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.shaikhabdulgani.todoapp.R
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityMainBinding
import com.shaikhabdulgani.todoapp.repo.UserRepo
import com.shaikhabdulgani.todoapp.util.GeneralEvent
import com.shaikhabdulgani.todoapp.util.UserInputEvent
import com.shaikhabdulgani.todoapp.util.toast
import com.shaikhabdulgani.todoapp.viewmodel.LoginViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.LoginViewModelFactory

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpViewModel()
        setUpToolbar()
        setUpProgressDialog()
        setUpObserver()
        setUpTextWatcher()

        binding.loginBtn.setOnClickListener {
            viewModel.onEvent(UserInputEvent.Submit)
        }
    }

    private fun setUpTextWatcher() {
        binding.usernameEdt.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(UserInputEvent.UsernameChange(text.toString()))
        }
        binding.passwordEdt.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(UserInputEvent.PasswordChange(text.toString()))
        }
    }

    private fun setUpObserver() {
        viewModel.loginState.observe(this) {
            if (it.usernameError.isBlank()) {
                binding.usernameEdt.error = null
            } else {
                binding.usernameEdt.error = it.usernameError
            }

            if (it.passwordError.isBlank()) {
                binding.passwordEdt.error = null
            } else {
                binding.passwordEdt.error = it.passwordError
            }
        }

        viewModel.authEvent.observe(this) { event ->
            when (event) {
                is GeneralEvent.Failed -> {
                    event.message?.let { toast(it) }
                    dismissProgressDialog()
                }

                is GeneralEvent.Success -> {
                    dismissProgressDialog()
                    event.message?.let {
                        toast(it)
                    }
                    Intent(this, ActivityHome::class.java).also {
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                }

                GeneralEvent.None -> dismissProgressDialog()
                GeneralEvent.Loading -> showProgressDialog()
            }
        }
    }

    private fun setUpProgressDialog() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            progressDialog = this
        }
    }

    private fun showProgressDialog() {
        if (!progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    private fun dismissProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun setUpViewModel() {
        val userRepository = UserRepo(AppDatabase(this))
        val viewModelFactory = LoginViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.loginToolbar.toolbarDefault)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}