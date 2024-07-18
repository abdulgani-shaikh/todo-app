package com.shaikhabdulgani.todoapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.shaikhabdulgani.todoapp.R
import com.shaikhabdulgani.todoapp.data.model.Todo
import com.shaikhabdulgani.todoapp.databinding.ItemTodoBinding

class TodoAdapter(private val actionBtnVisible: Boolean = true) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    interface TodoClickListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onCompleteClick(position: Int)
    }

    companion object {
        const val TAG = "TodoAdapter"
    }

    private var listener: TodoClickListener = object : TodoClickListener {
        override fun onEditClick(position: Int) {
            Log.d(TAG, "Not Implemented")
        }

        override fun onDeleteClick(position: Int) {
            Log.d(TAG, "Not Implemented")
        }

        override fun onCompleteClick(position: Int) {
            Log.d(TAG, "Not Implemented")
        }
    }

    fun setOnCLickListener(listener: TodoClickListener) {
        this.listener = listener
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }

    val differList = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differList.currentList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = differList.currentList[position]
        holder.binding.apply {
            todoTitleText.text = todo.title
            todoSubtitleText.text = todo.subtitle
            if (todo.isCompleted) {
                rootLayout.background = ContextCompat.getDrawable(root.context,R.drawable.completed_todo_background)
                actionBtnGroup.visibility = View.GONE
            }
        }
    }

    inner class TodoViewHolder(val binding: ItemTodoBinding) : ViewHolder(binding.root) {

        private fun initializedProperties() {
            if (!actionBtnVisible) {
                binding.actionBtnGroup.visibility = View.GONE
                return
            }
            binding.editBtn.setOnClickListener {
                if (adapterPosition == NO_POSITION) {
                    return@setOnClickListener
                }
                listener.onEditClick(adapterPosition)
            }

            binding.deleteBtn.setOnClickListener {
                if (adapterPosition == NO_POSITION) {
                    return@setOnClickListener
                }
                listener.onDeleteClick(adapterPosition)
            }

            binding.completeBtn.setOnClickListener {
                if (adapterPosition == NO_POSITION) {
                    return@setOnClickListener
                }
                listener.onCompleteClick(adapterPosition)
            }
        }

        init {
            initializedProperties()
        }
    }
}