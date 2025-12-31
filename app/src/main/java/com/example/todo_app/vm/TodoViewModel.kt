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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TodoViewModel(app: Application) : AndroidViewModel(app) {

    private val repo: TodoRepo = TodoRepo(TodoDatabase.getDatabase(app).todoDao())

    // currently selected category
    private val _currentCategory = MutableStateFlow(TodoCategory.WORK)
    val currentCategory: StateFlow<TodoCategory> = _currentCategory.asStateFlow()

    // todos shown in UI
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    init {
        viewModelScope.launch {
            _currentCategory
                .flatMapLatest { category ->
                    repo.getTodosByCategory(category)
                }
                .collect { list ->

                    _todos.value = list.sortedWith(
                        compareBy<Todo> { it.done }.thenByDescending { it.createdAt }
                    )
                }
        }
    }

    fun addTodo(title: String, category: TodoCategory) {
        val clean = title.trim()
        if (clean.isBlank()) return

        viewModelScope.launch {
            repo.add(Todo(title = clean, category = category))
        }
    }

    fun toggleDone(todo: Todo) {
        viewModelScope.launch {
            repo.update(todo.copy(done = !todo.done))
        }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch {
            repo.delete(todo)
        }
    }

    fun setCategory(category: TodoCategory) {
        _currentCategory.value = category
    }
}
