package com.example.todoapp.data.client

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.ioc.ToDoApplication
import com.example.todoapp.ui.model.TodoItem

@Database(entities = [TodoItem::class], version = 24, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class ToDoDB : RoomDatabase() {
    abstract fun mainDAO(): DAO

    companion object {

        @Volatile
        private var INSTANCE: ToDoDB? = null

        fun getDatabase(application: ToDoApplication): ToDoDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    application.applicationContext,
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