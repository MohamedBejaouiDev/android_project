package com.example.todo_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id:Long = 1,
    val title:String,
    val done:Boolean = false,
    var createdAt: Long = System.currentTimeMillis(),
    val category: TodoCategory = TodoCategory.OTHERS
)
