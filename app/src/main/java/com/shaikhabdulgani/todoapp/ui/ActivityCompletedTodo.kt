package com.shaikhabdulgani.todoapp.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shaikhabdulgani.todoapp.adapter.TodoAdapter
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityCompletedTodoBinding
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.viewmodel.HomeViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.HomeViewModelFactory

class ActivityCompletedTodo : AppCompatActivity() {

    companion object{
        private const val TAG = "ActivityCompletedTodo"
    }

    private val binding: ActivityCompletedTodoBinding by lazy {
        ActivityCompletedTodoBinding.inflate(layoutInflater)
    }
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpToolbar()
        setUpViewModel()
        setUpTodoRecycler()
        setUpObserver()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.tbComplete.toolbarDefault)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Completed Todo"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpTodoRecycler() {
        todoAdapter = TodoAdapter(false)
        binding.rvCompletedTodo.layoutManager = LinearLayoutManager(this)
        binding.rvCompletedTodo.adapter = todoAdapter
        todoAdapter.setOnCLickListener(object : TodoAdapter.TodoClickListener {
            override fun onEditClick(position: Int) {
            }

            override fun onDeleteClick(position: Int) {
                Log.d(TAG,"Vlaornt")
                deleteTodo(position)
            }

            override fun onCompleteClick(position: Int) {

            }
        })
    }

    private fun setUpViewModel() {
        val repo = TodoRepo(AppDatabase(this))
        val viewModelFactory = HomeViewModelFactory(repo)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun setUpObserver() {
        viewModel.getAllTodo()
        viewModel.todo.observe(this) {
            todoAdapter.setList(it.filter { todo -> todo.isCompleted })
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
}