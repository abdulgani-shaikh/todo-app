package com.shaikhabdulgani.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val title:String = "TODO TITLE",
    val subtitle:String = "TODO SUBTITLE",
    val isCompleted: Boolean = false,
    val timestamp:Long = System.currentTimeMillis(),
){
    companion object{
        const val ID = "id"
        const val TITLE = "title"
        const val SUBTITLE = "subtitle"
        const val TIMESTAMP = "timestamp"
    }
}
