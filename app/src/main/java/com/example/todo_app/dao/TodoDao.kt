package com.example.todo_app.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo_app.data.Todo
import com.example.todo_app.data.TodoCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM Todos ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Todo>>

    @Query("SELECT * FROM Todos WHERE category = :category ORDER BY createdAt DESC")
    fun getTodosByCategory(category: TodoCategory): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}