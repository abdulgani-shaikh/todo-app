package com.shaikhabdulgani.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shaikhabdulgani.todoapp.dao.TodoDao
import com.shaikhabdulgani.todoapp.data.model.Todo

@Database(
    version = 1,
    entities = [Todo::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
    companion object {
        private var instance: AppDatabase? = null
        private val lock = Any()
        operator fun invoke(context: Context): AppDatabase = instance ?: synchronized(lock) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                .build()
        }
    }
}