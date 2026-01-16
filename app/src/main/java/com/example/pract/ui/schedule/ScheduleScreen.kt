package com.example.pract.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pract.data.dto.GroupDto
import com.example.pract.data.dto.ScheduleByDateDto
import com.example.pract.data.repository.ScheduleRepository
import com.example.pract.utils.addFavorite
import com.example.pract.utils.getWeekDateRange
import com.example.pract.utils.removeFavorite
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    repository: ScheduleRepository,
    selectedGroup: String,
    onSelectedGroupChange: (String) -> Unit,
    favorites: Set<String>,
    groupsLoaded: Boolean,
    groups: List<GroupDto>
) {
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var searchText by remember { mutableStateOf(selectedGroup) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
        !groupsLoaded || loading -> CircularProgressIndicator(
            modifier = Modifier.padding(16.dp)
        )
        error != null -> Text("Ошибка: $error")
        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.weight(1f)
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
                                            onSelectedGroupChange(group.groupName)
                                            searchText = group.groupName
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (favorites.contains(selectedGroup)) {
                                    removeFavorite(context, selectedGroup)
                                } else {
                                    addFavorite(context, selectedGroup)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (favorites.contains(selectedGroup)) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Добавить в избранное",
                            tint = if (favorites.contains(selectedGroup)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                ScheduleList(schedule, Modifier.weight(1f))
            }
        }
    }
}