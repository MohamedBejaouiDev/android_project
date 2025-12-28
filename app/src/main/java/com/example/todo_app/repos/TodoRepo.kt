package com.example.todo_app.repos

import com.example.todo_app.data.Todo
import com.example.todo_app.data.TodoCategory
import com.example.todo_app.dao.TodoDao
import kotlinx.coroutines.flow.Flow

class TodoRepo(private val dao: TodoDao) {
    fun getAll(): Flow<List<Todo>> = dao.getAll()
    fun getTodosByCategory(category: TodoCategory): Flow<List<Todo>> = dao.getTodosByCategory(category)
    suspend fun add(todo: Todo) = dao.insert(todo)
    suspend fun update(todo: Todo) = dao.update(todo)
    suspend fun delete(todo: Todo) = dao.delete(todo)
}
