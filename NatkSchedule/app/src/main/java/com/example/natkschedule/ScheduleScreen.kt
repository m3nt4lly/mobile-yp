package com.example.natkschedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Screen {
    Schedule, Favorites
}

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val filteredGroups by viewModel.filteredGroupsFlow.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.Schedule) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (currentScreen == Screen.Schedule) "Расписание НАТК" else "Избранные группы", 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.Schedule,
                    onClick = { currentScreen = Screen.Schedule },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Расписание") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Favorites,
                    onClick = { currentScreen = Screen.Favorites },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Избранное") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (currentScreen) {
                Screen.Schedule -> {
                    ScheduleTab(
                        viewModel = viewModel,
                        searchQuery = searchQuery,
                        selectedGroup = selectedGroup,
                        favorites = favorites,
                        filteredGroups = filteredGroups,
                        schedule = schedule,
                        isLoading = isLoading,
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    )
                }
                Screen.Favorites -> {
                    FavoritesTab(
                        favorites = favorites.toList(),
                        onFavoriteClick = { group ->
                            viewModel.selectGroup(group)
                            currentScreen = Screen.Schedule
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleTab(
    viewModel: ScheduleViewModel,
    searchQuery: String,
    selectedGroup: String,
    favorites: Set<String>,
    filteredGroups: List<String>,
    schedule: List<ScheduleByDateDto>,
    isLoading: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search and Selection
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    viewModel.onSearchQueryChange(it)
                    onExpandedChange(true) 
                },
                label = { Text("Введите название группы") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (selectedGroup.isNotEmpty()) {
                        IconButton(onClick = { viewModel.toggleFavorite(selectedGroup) }) {
                            Icon(
                                imageVector = if (favorites.contains(selectedGroup)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (favorites.contains(selectedGroup)) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().onFocusChanged { state ->
                    if (state.isFocused) onExpandedChange(true)
                },
                singleLine = true
            )
            
            DropdownMenu(
                expanded = expanded && filteredGroups.isNotEmpty(),
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.fillMaxWidth(0.9f).heightIn(max = 300.dp)
            ) {
                filteredGroups.forEach { group ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (favorites.contains(group)) {
                                    Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(group)
                            }
                        },
                        onClick = {
                            viewModel.selectGroup(group)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (selectedGroup.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Выберите группу для просмотра расписания",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else if (schedule.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Расписание не найдено", style = MaterialTheme.typography.titleMedium)
                    Text("Проверьте подключение к серверу", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(schedule) { daySchedule ->
                    DayScheduleItem(daySchedule)
                }
            }
        }
    }
}

@Composable
fun FavoritesTab(
    favorites: List<String>,
    onFavoriteClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("У вас пока нет избранных групп", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { group ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onFavoriteClick(group) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(group, style = MaterialTheme.typography.titleMedium)
                        }
                        IconButton(onClick = { onToggleFavorite(group) }) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayScheduleItem(daySchedule: ScheduleByDateDto) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${daySchedule.date} (${daySchedule.weekdayName})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        if (daySchedule.lessons.isEmpty()) {
            Text("Нет пар", modifier = Modifier.padding(start = 32.dp), style = MaterialTheme.typography.bodyMedium)
        } else {
            daySchedule.lessons.forEach { lesson ->
                LessonCard(lesson)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
