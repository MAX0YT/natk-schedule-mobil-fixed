package com.example.pract.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.pract.data.dto.GroupDto
import com.example.pract.data.dto.ScheduleByDateDto
import com.example.pract.data.repository.ScheduleRepository
import com.example.pract.utils.getWeekDateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(repository: ScheduleRepository) {
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var groups by remember { mutableStateOf<List<GroupDto>>(emptyList()) }
    var selectedGroup by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var loadingGroups by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var errorGroups by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            groups = repository.getGroups()
            if (groups.isNotEmpty()) {
                selectedGroup = groups.find { it.groupName == "ИС-12" }?.groupName ?: groups[0].groupName
                searchText = selectedGroup
            }
        } catch (e: Exception) {
            errorGroups = e.message
        } finally {
            loadingGroups = false
        }
    }

    LaunchedEffect(selectedGroup) {
        if (selectedGroup.isNotEmpty()) {
            loading = true
            val (start, end) = getWeekDateRange()
            try {
                schedule = repository.loadSchedule(selectedGroup, start, end)
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    when {
        loadingGroups || loading -> CircularProgressIndicator(
            modifier = Modifier.padding(16.dp)
        )
        errorGroups != null -> Text("Ошибка загрузки групп: $errorGroups")
        error != null -> Text("Ошибка: $error")
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.padding(16.dp)
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Выберите группу") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .onFocusChanged {
                                if (it.isFocused) {
                                    expanded = true
                                }
                            }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val filteredGroups = groups.filter {
                            it.groupName.contains(searchText, ignoreCase = true)
                        }
                        if (filteredGroups.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Нет совпадений") },
                                onClick = { },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        } else {
                            filteredGroups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.groupName) },
                                    onClick = {
                                        selectedGroup = group.groupName
                                        searchText = group.groupName
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
                ScheduleList(schedule, Modifier.weight(1f))
            }
        }
    }
}