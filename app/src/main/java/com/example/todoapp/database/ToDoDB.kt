package com.example.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.model.TodoItem

// Создание базы данных
@Database(entities = [TodoItem::class], version = 10, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class ToDoDB() : RoomDatabase() {
    abstract fun mainDAO(): DAO

    companion object {

        @Volatile
        private var INSTANCE: ToDoDB? = null

        fun getDatabase(context: Context): ToDoDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDB::class.java,
                    "ToDo_DataBase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}