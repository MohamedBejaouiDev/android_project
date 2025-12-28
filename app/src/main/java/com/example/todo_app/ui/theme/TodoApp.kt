package com.example.todo_app.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todo_app.Screen
import com.example.todo_app.blog.BlogModule
import com.example.todo_app.blog.ui.BlogDetailScreen
import com.example.todo_app.blog.ui.BlogScreen
import com.example.todo_app.data.BlogItem
import com.example.todo_app.data.Todo
import com.example.todo_app.data.TodoCategory
import com.example.todo_app.vm.BlogViewModel
import com.example.todo_app.vm.TodoViewModel

@Composable
fun TodoApp(
    todoViewModel: TodoViewModel = viewModel(),
    blogViewModel: BlogViewModel = viewModel(
        factory = BlogModule.provideBlogViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    ),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TodoList.route,
        modifier = modifier
    ) {
        composable(Screen.TodoList.route) {
            TodoListContainer(
                todoViewModel = todoViewModel,
                onNavigateToBlogs = { navController.navigate(Screen.Blog.route) },
                navController = navController
            )
        }

        composable(Screen.Blog.route) {
            BlogScreen(
                blogViewModel = blogViewModel,
                onBack = { navController.popBackStack() },
                onBlogClick = { blog ->
                    // Save the clicked blog then navigate to details
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("blog", blog)

                    navController.navigate(Screen.BlogDetail.route)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Screen.BlogDetail.route) {
            val blog: BlogItem? = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get("blog")

            if (blog != null) {
                BlogDetailScreen(
                    blog = blog,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No blog selected")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListContainer(
    todoViewModel: TodoViewModel,
    onNavigateToBlogs: () -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }
    val todos by todoViewModel.todos.collectAsState()
    val currentCategory by todoViewModel.currentCategory.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(tonalElevation = 2.dp) {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "Todo List",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        actions = {
                            TextButton(
                                onClick = onNavigateToBlogs,
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Article,
                                    contentDescription = "Blogs",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Blogs")
                            }
                        }
                    )

                    CategoryTabs(
                        currentCategory = currentCategory,
                        onCategorySelected = { todoViewModel.setCategory(it) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Todo")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (todos.isEmpty()) {
                EmptyState(
                    category = currentCategory,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = todos,
                        key = { it.id }
                    ) { todo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            TodoRow(
                                todo = todo,
                                onToggle = { todoViewModel.toggleDone(it) },
                                onDeleteClick = { todoToDelete = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTodoDialog(
            onAdd = { title, category ->
                todoViewModel.addTodo(title, category)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    todoToDelete?.let { todo ->
        DeleteConfirmationDialog(
            todoTitle = todo.title,
            onConfirmDelete = {
                todoViewModel.delete(todo)
                todoToDelete = null
            },
            onDismiss = { todoToDelete = null }
        )
    }
}

@Composable
fun CategoryTabs(
    currentCategory: TodoCategory,
    onCategorySelected: (TodoCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = TodoCategory.entries

    TabRow(
        selectedTabIndex = categories.indexOf(currentCategory),
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        categories.forEach { category ->
            val label = category.name
                .lowercase()
                .replaceFirstChar { it.titlecase() }

            Tab(
                selected = currentCategory == category,
                onClick = { onCategorySelected(category) },
                text = { Text(label) }
            )
        }
    }
}

@Composable
fun EmptyState(category: TodoCategory, modifier: Modifier = Modifier) {
    val prettyName = category.name.lowercase()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircleOutline,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "No $prettyName tasks yet",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tap “+” to add your first one.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
