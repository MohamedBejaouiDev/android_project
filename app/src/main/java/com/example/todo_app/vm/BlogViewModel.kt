package com.example.todo_app.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_app.data.BlogItem
import com.example.todo_app.repos.BlogRepository
import com.example.todo_app.data.TodoCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BlogUiState {
    object Loading : BlogUiState()
    data class Success(val blogs: List<BlogItem>) : BlogUiState()
    data class Error(val message: String) : BlogUiState()
}

class BlogViewModel(
    application: Application,
    private val repository: BlogRepository
): AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    private val _currentCategory = MutableStateFlow(TodoCategory.WORK)
    val currentCategory: StateFlow<TodoCategory> = _currentCategory.asStateFlow()

    init {
        // Load initial blogs based on the default category
        loadBlogs(_currentCategory.value)
    }

    fun loadBlogs(category: TodoCategory) {
        _currentCategory.value = category
        _uiState.value = BlogUiState.Loading
        viewModelScope.launch {
            val result = repository.fetchBlogs(category)
            result.onSuccess { blogs ->
                _uiState.value = BlogUiState.Success(blogs)
            }.onFailure { e ->
                _uiState.value = BlogUiState.Error(e.localizedMessage ?: "An unknown error occurred")
            }
        }
    }
}