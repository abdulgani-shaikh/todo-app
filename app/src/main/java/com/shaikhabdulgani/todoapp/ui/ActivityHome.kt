package com.shaikhabdulgani.todoapp.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shaikhabdulgani.todoapp.R
import com.shaikhabdulgani.todoapp.adapter.TodoAdapter
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityHomeBinding
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.viewmodel.HomeViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.HomeViewModelFactory

class ActivityHome : AppCompatActivity() {

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


        binding.bottomNavView.setOnItemSelectedListener {
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
        binding.allTodoRv.layoutManager = LinearLayoutManager(this)
        binding.allTodoRv.adapter = todoAdapter
    }

    private fun setUpViewModel() {
        val repo = TodoRepo(AppDatabase(this))
        val viewModelFactory = HomeViewModelFactory(repo)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun setUpObserver() {
        viewModel.getAllTodo().observe(this) {
            todoAdapter.differList.submitList(it)
        }
    }

    private fun deleteTodo(position: Int){
        AlertDialog.Builder(this).apply {
            setTitle("Delete Todo")
            setMessage("Are you sure you want to delete this todo")
            setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTodo(todoAdapter.differList.currentList[position])
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    private fun completeTodo(position: Int){
        AlertDialog.Builder(this).apply {
            setTitle("Add to completed")
            setMessage("Are you sure you want to add this todo to completed")
            setPositiveButton("Yes") { _, _ ->
                viewModel.setComplete(todoAdapter.differList.currentList[position])
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    private fun setUpClickListener() {
        binding.addTodoFab.setOnClickListener {
            Intent(binding.root.context, ActivityAddTodo::class.java).also {
                startActivity(it)
            }
        }

        todoAdapter.setOnCLickListener(object : TodoAdapter.TodoClickListener {
            override fun onEditClick(position: Int) {
                Intent(this@ActivityHome, ActivityEditTodo::class.java).apply {
                    putExtra(Todo.ID, todoAdapter.differList.currentList[position].id)
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
}

