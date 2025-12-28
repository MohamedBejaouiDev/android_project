package com.example.todo_app

import com.example.todo_app.data.TodoCategory

sealed class Screen(val route: String) {
    data object TodoList : Screen("todo_list")
    data object Blog : Screen("blog")
    data object BlogDetail : Screen("blog_detail")
}