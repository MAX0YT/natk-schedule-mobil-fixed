package com.example.pract.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.pract.data.dto.LessonDto
import com.example.pract.data.dto.LessonGroupPart
import com.example.pract.data.dto.LessonPartDto
import com.example.pract.data.dto.ScheduleByDateDto

@Composable
fun ScheduleList(
    data: List<ScheduleByDateDto>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(data) { day ->
            Text(
                text = "${day.lessonDate} (${day.weekday})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (day.lessons.isEmpty()) {
                Text(
                    text = "Информация отсутствует",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                )
            } else {
                day.lessons.forEach { lesson ->
                    LessonCard(lesson)
                }
            }
        }
    }
}

@Composable
fun LessonCard(lesson: LessonDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Пара ${lesson.lessonNumber} (${lesson.time})",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            lesson.groupParts.forEach { (part, info) ->
                if (info != null) {
                    LessonPartRow(part, info)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun LessonPartRow(part: LessonGroupPart, info: LessonPartDto) {
    val lessonTypeIcon = getLessonTypeIcon(info.subject)
    val buildingColor = getBuildingColor(info.building)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = lessonTypeIcon,
            contentDescription = "Иконка типа занятия",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = "$part: ${info.subject}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = info.teacher,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${info.building}, ${info.classroom}",
                style = MaterialTheme.typography.labelMedium,
                color = buildingColor
            )
        }
    }
}

fun getLessonTypeIcon(subject: String): ImageVector {
    return when {
        subject.lowercase().contains("лекц") -> Icons.Default.Book
        subject.lowercase().contains("практ") || subject.lowercase().contains("лаб") -> Icons.Default.Computer
        subject.lowercase().contains("семин") -> Icons.Default.School
        else -> Icons.Default.Work
    }
}

@Composable
fun getBuildingColor(building: String): Color {
    return when (building) {
        "Корпус инженерии" -> Color(0xFF2196F3) // Синий
        "Учебный корпус №2" -> Color(0xFF4CAF50) // Зеленый
        "Спортивный корпус" -> Color(0xFFFFC107) // Желтый
        "Лабораторный корпус" -> Color(0xFF21CCF3) // Желтый
        "Главный корпус" -> Color(0xFFE91E63) // Розовый
        "Физический корпус" -> Color(0xFFE91EFF) // Розовый
        "Корпус Психологии" -> Color(0xFFCCAA22) // Розовый
        "Корпус Экономики" -> Color(0xFF00C107) // Розовый
        "Институт ИТ" -> Color(0xFF0066F3) // Розовый
        "Библиотечный центр" -> Color(0xFF555555) // Розовый
        else -> MaterialTheme.colorScheme.secondary
    }
}