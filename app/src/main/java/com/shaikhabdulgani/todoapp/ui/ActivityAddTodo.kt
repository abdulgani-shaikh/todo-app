package com.shaikhabdulgani.todoapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shaikhabdulgani.todoapp.R
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityAddTodoBinding
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.util.GeneralEvent
import com.shaikhabdulgani.todoapp.util.TodoInputEvent
import com.shaikhabdulgani.todoapp.viewmodel.SaveTodoViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.SaveTodoViewModelFactory

class ActivityAddTodo : AppCompatActivity() {

    companion object {
        const val TAG = "ActivityAddTodo"
    }

    private val binding: ActivityAddTodoBinding by lazy {
        ActivityAddTodoBinding.inflate(layoutInflater)
    }

    private lateinit var viewmodel: SaveTodoViewModel
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpToolbar()
        setUpViewModel()
        setUpTextWatcher()
        setUpProgressDialog()
        setUpObservers()

        binding.btnAddTodo.setOnClickListener {
            viewmodel.onEvent(TodoInputEvent.Submit)
        }
    }

    private fun setUpObservers() {
        viewmodel.todoState.observe(this) {
            binding.apply {
                if (it.titleError.isBlank()) {
                    edtTitleAdd.error = null
                } else {
                    edtTitleAdd.error = it.titleError
                }

                if (it.subtitleError.isBlank()) {
                    edtSubtitleAdd.error = null
                } else {
                    edtSubtitleAdd.error = it.subtitleError
                }
            }
        }

        viewmodel.generalEvent.observe(this) {
            when (it) {
                GeneralEvent.None -> dismissProgressDialog()
                GeneralEvent.Loading -> showProgressDialog()
                is GeneralEvent.Failed -> {
                    Snackbar.make(binding.root, "Failed to add todo", Snackbar.LENGTH_SHORT).show()
                    dismissProgressDialog()
                }

                is GeneralEvent.Success -> {
                    Snackbar.make(binding.root, "Todo added successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    dismissProgressDialog()
                    finish()
                }
            }
        }
    }

    private fun setUpTextWatcher() {
        binding.edtTitleAdd.doOnTextChanged { text, _, _, _ ->
            viewmodel.onEvent(TodoInputEvent.TitleChange(text.toString()))
        }
        binding.edtSubtitleAdd.doOnTextChanged { text, _, _, _ ->
            viewmodel.onEvent(TodoInputEvent.SubtitleChange(text.toString()))
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
        val repo = TodoRepo(AppDatabase(this))
        val viewModelFactory = SaveTodoViewModelFactory(repo)
        viewmodel = ViewModelProvider(this, viewModelFactory)[SaveTodoViewModel::class.java]
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.tbAddTodo.toolbarDefault)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Todo"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}