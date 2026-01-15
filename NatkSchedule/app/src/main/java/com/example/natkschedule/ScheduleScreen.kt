package com.example.natkschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    val groups by viewModel.groups.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Dropdown выбора группы
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedGroup,
                onValueChange = {},
                readOnly = true,
                label = { Text("Выберите группу") },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown, "contentDescription",
                        Modifier.clickable { expanded = !expanded }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                groups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group) },
                        onClick = {
                            viewModel.selectGroup(group)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(schedule) { daySchedule ->
                    DayHeader(daySchedule)
                    daySchedule.lessons.forEach { lesson ->
                        LessonCard(lesson)
                    }
                }
            }
        }
    }
}

@Composable
fun DayHeader(daySchedule: ScheduleByDateDto) {
    Text(
        text = "${daySchedule.date} (${daySchedule.weekdayName})",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun LessonCard(lesson: LessonDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${lesson.lessonNumber} пара",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${lesson.timeStart} - ${lesson.timeEnd}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            if (lesson.parts.isEmpty()) {
                Text("Нет занятий", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            } else {
                lesson.parts.forEach { (partName, partInfo) ->
                    Column(modifier = Modifier.padding(bottom = 4.dp)) {
                        if (partName != "FULL") {
                            Text(
                                text = if (partName == "SUB1") "Подгруппа 1" else "Подгруппа 2",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = partInfo.subjectName,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${partInfo.teacherName} | ${partInfo.classroomNumber} (${partInfo.buildingName})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
