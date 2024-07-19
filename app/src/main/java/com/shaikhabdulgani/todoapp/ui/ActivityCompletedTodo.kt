package com.shaikhabdulgani.todoapp.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shaikhabdulgani.todoapp.adapter.TodoAdapter
import com.shaikhabdulgani.todoapp.database.AppDatabase
import com.shaikhabdulgani.todoapp.databinding.ActivityCompletedTodoBinding
import com.shaikhabdulgani.todoapp.repo.TodoRepo
import com.shaikhabdulgani.todoapp.viewmodel.HomeViewModel
import com.shaikhabdulgani.todoapp.viewmodel.factory.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ActivityCompletedTodo : AppCompatActivity() {

    private val binding: ActivityCompletedTodoBinding by lazy {
        ActivityCompletedTodoBinding.inflate(layoutInflater)
    }
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpTodoRecycler()
        setUpViewModel()
        setUpObserver()
        setUpToolbar()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbarComplete.toolbarDefault)
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
        binding.completedTodoRv.layoutManager = LinearLayoutManager(this)
        binding.completedTodoRv.adapter = todoAdapter
    }

    private fun setUpViewModel() {
        val repo = TodoRepo(AppDatabase(this))
        val viewModelFactory = HomeViewModelFactory(repo)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    private fun setUpObserver() {
        viewModel.getAllTodo()
//        lifecycleScope.launch {
//            viewModel.todo.collectLatest {
//                todoAdapter.setList(it.filter { todo -> todo.isCompleted })
////                todoAdapter.differList.submitList(it.filter { todo -> todo.isCompleted })
//            }
//        }
        viewModel.todo.observe(this){
            todoAdapter.setList(it.filter { todo -> todo.isCompleted })
        }
    }
}