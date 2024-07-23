package com.shaikhabdulgani.todoapp.ui

import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shaikhabdulgani.todoapp.R
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityEditTodoBinding
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.util.GeneralEvent
import com.shaikhabdulgani.todoapp.util.TodoInputEvent
import com.shaikhabdulgani.todoapp.viewmodel.SaveTodoViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.SaveTodoViewModelFactory

class ActivityEditTodo : AppCompatActivity() {

    companion object {
        const val TAG = "ActivityEditTodo"
    }

    private val binding: ActivityEditTodoBinding by lazy {
        ActivityEditTodoBinding.inflate(layoutInflater)
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

        binding.btnUpdateTodo.setOnClickListener {
            updateTodo()
        }
        binding.btnCancelTodo.setOnClickListener {
            finish()
        }

    }

    private fun updateTodo() {
        AlertDialog.Builder(this).apply {
            setTitle("Save changes?")
            setMessage("Are you sure you want to save these changes")
            setPositiveButton("Yes") { _, _ ->
                viewmodel.onEvent(TodoInputEvent.Submit)
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    private fun setUpObservers() {
        viewmodel.todoState.observe(this) {
            binding.apply {
                if (edtTitle.text.toString() != it.title) {
                    edtTitle.setText(it.title)
                }
                if (edtSubtitle.text.toString() != it.subtitle) {
                    edtSubtitle.setText(it.subtitle)
                }
                if (it.titleError.isBlank()) {
                    edtTitle.error = null
                } else {
                    edtTitle.error = it.titleError
                }

                if (it.subtitleError.isBlank()) {
                    edtSubtitle.error = null
                } else {
                    edtSubtitle.error = it.subtitleError
                }
            }
        }

        viewmodel.generalEvent.observe(this) {
            when (it) {
                GeneralEvent.None -> dismissProgressDialog()
                GeneralEvent.Loading -> showProgressDialog()
                is GeneralEvent.Failed -> {
                    dismissProgressDialog()
                    Snackbar.make(binding.root, "Failed to add todo", Snackbar.LENGTH_SHORT).show()
                }

                is GeneralEvent.Success -> {
                    dismissProgressDialog()
                    Snackbar.make(binding.root, "Todo updated successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }

    private fun setUpTextWatcher() {
        binding.edtTitle.doOnTextChanged { text, _, _, _ ->
            viewmodel.onEvent(TodoInputEvent.TitleChange(text.toString()))
        }
        binding.edtSubtitle.doOnTextChanged { text, _, _, _ ->
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

        val id = intent.getLongExtra(Todo.ID, -1)
        println("$id")
        viewmodel.onEvent(TodoInputEvent.InitTodo(id))
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.tbEditTodo.toolbarDefault)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Todo"
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