package com.example.todo_app.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_app.data.Todo
import com.example.todo_app.data.TodoCategory
import com.example.todo_app.db.TodoDatabase
import com.example.todo_app.repos.TodoRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel(app: Application): AndroidViewModel(app) {
    private val repo: TodoRepo
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _currentCategory = MutableStateFlow(TodoCategory.OTHERS)
    val currentCategory: StateFlow<TodoCategory> = _currentCategory.asStateFlow()

    init {
        val db = TodoDatabase.getDatabase(app)
        repo = TodoRepo(db.todoDao())
        // Load todos for the default category on initialization
        loadTodosForCategory(_currentCategory.value)
    }

    private fun loadTodosForCategory(category: TodoCategory) {
        viewModelScope.launch {
            repo.getTodosByCategory(category).collect { list: List<Todo> -> // Explicitly typed parameter
                _todos.value = list
            }
        }
    }

    fun addTodo(title: String, category: TodoCategory){
        if (title.isBlank()) return
        viewModelScope.launch {
            repo.add(Todo(title = title.trim(), category = category))
            // Reload todos for the current category after adding
            loadTodosForCategory(_currentCategory.value)
        }
    }

    fun toggleDone(todo: Todo){
        viewModelScope.launch {
            repo.update(todo.copy(done = !todo.done))
            // Reload todos for the current category after toggling
            loadTodosForCategory(_currentCategory.value)
        }
    }

    fun delete(todo:Todo){
        viewModelScope.launch {
            repo.delete(todo)
            // Reload todos for the current category after deletion
            loadTodosForCategory(_currentCategory.value)
        }
    }

    fun setCategory(category: TodoCategory) {
        _currentCategory.value = category
        loadTodosForCategory(category)
    }
}