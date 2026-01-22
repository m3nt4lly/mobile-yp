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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val filteredGroups by viewModel.filteredGroupsFlow.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Расписание НАТК", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search and Selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            viewModel.onSearchQueryChange(it)
                            expanded = true 
                        },
                        label = { Text("Поиск группы") },
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
                             if (state.isFocused) expanded = true
                        },
                        singleLine = true
                    )
                    
                    DropdownMenu(
                        expanded = expanded && filteredGroups.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f).heightIn(max = 300.dp)
                    ) {
                        // Show Favorites First
                        val favoriteMatches = filteredGroups.filter { favorites.contains(it) }
                        val otherMatches = filteredGroups.filter { !favorites.contains(it) }

                        if (favoriteMatches.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Избранное", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) },
                                onClick = {},
                                enabled = false
                            )
                            favoriteMatches.forEach { group ->
                                GroupDropdownItem(group, true) {
                                    viewModel.selectGroup(group)
                                    expanded = false
                                }
                            }
                            Divider()
                        }

                        otherMatches.forEach { group ->
                            GroupDropdownItem(group, false) {
                                viewModel.selectGroup(group)
                                expanded = false
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(schedule) { daySchedule ->
                        DayScheduleItem(daySchedule)
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
            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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


