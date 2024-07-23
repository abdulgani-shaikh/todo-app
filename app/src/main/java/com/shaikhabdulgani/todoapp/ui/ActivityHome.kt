package com.shaikhabdulgani.todoapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.shaikhabdulgani.todoapp.R
import com.shaikhabdulgani.todoapp.adapter.TodoAdapter
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityHomeBinding
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.viewmodel.HomeViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.HomeViewModelFactory
import kotlin.time.Duration.Companion.days

class ActivityHome : AppCompatActivity() {

    companion object{
        const val TAG = "ActivityHome"
    }

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpViewModel()
        setUpTodoRecycler()
        setUpObserver()
        setUpClickListener()
        setUpToolbar()


        binding.bnvHome.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.completed_todo_menu_item -> {
                    Intent(this, ActivityCompletedTodo::class.java).apply {
                        startActivity(this)
                    }
                    true
                }

                else -> true
            }
        }
    }

    private fun setUpTodoRecycler() {
        todoAdapter = TodoAdapter()
        binding.rvAllTodo.layoutManager = LinearLayoutManager(this)
        binding.rvAllTodo.adapter = todoAdapter
    }

    private fun setUpViewModel() {
        val repo = TodoRepo(AppDatabase(this))
        val viewModelFactory = HomeViewModelFactory(repo)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun setUpObserver() {
        viewModel.getAllTodo()
        viewModel.todo.observe(this){
            todoAdapter.setList(it)
        }
    }

    private fun deleteTodo(position: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Delete Todo")
            setMessage("Are you sure you want to delete this todo")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTodo(todoAdapter.getItem(position))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    private fun completeTodo(position: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Add to completed")
            setMessage("Are you sure you want to add this todo to completed")
            setPositiveButton("Yes") { _, _ ->
                viewModel.setComplete(todoAdapter.getItem(position))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    private fun setUpClickListener() {
        binding.fabClearFilter.isVisible = false
        binding.fabAddTodo.setOnClickListener {
            Intent(binding.root.context, ActivityAddTodo::class.java).also {
                startActivity(it)
            }
        }

        binding.fabClearFilter.setOnClickListener {
            viewModel.getAllTodo()
            binding.fabClearFilter.isVisible = false
        }

        todoAdapter.setOnCLickListener(object : TodoAdapter.TodoClickListener {
            override fun onEditClick(position: Int) {
                Intent(this@ActivityHome, ActivityEditTodo::class.java).apply {
                    putExtra(Todo.ID, todoAdapter.getItem(position).id)
                    startActivity(this)
                }
            }

            override fun onDeleteClick(position: Int) {
                deleteTodo(position)
            }

            override fun onCompleteClick(position: Int) {
                completeTodo(position)
            }
        })
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select A Date Range")

        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.getAllTodo(it.first, it.second + 1.days.inWholeMilliseconds)
            binding.fabClearFilter.isVisible = true
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.tbHome.toolbarHome)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.calender_item -> showDatePicker()
        }
        return super.onOptionsItemSelected(item)
    }
}

