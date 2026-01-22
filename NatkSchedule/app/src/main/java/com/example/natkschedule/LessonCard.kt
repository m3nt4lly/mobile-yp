package com.example.natkschedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonCard(lesson: LessonDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Time Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = "${lesson.lessonNumber}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "пара",
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lesson.timeStart,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lesson.timeEnd,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Divider(modifier = Modifier.height(80.dp).width(1.dp), color = Color.Gray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.width(16.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                 if (lesson.parts.isEmpty()) {
                    Text("Свободно", style = MaterialTheme.typography.bodyLarge)
                } else {
                    lesson.parts.forEach { (partName, partInfo) ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            // Badge for Subgroup
                            if (partName != "FULL") {
                                Surface(
                                    color = if(partName == "SUB1") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                ) {
                                    Text(
                                        text = if (partName == "SUB1") "1 п/г" else "2 п/г",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = partInfo.subjectName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = partInfo.classroomNumber,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(" | ", color = Color.Gray)
                                Text(
                                    text = partInfo.teacherName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = partInfo.buildingName,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
