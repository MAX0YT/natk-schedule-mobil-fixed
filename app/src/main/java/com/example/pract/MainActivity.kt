package com.example.pract

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.pract.data.api.ScheduleApi
import com.example.pract.data.dto.GroupDto
import com.example.pract.data.repository.ScheduleRepository
import com.example.pract.ui.favorites.FavoritesScreen
import com.example.pract.ui.schedule.ScheduleScreen
import com.example.pract.ui.theme.PractTheme
import com.example.pract.utils.getFavoritesFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PractTheme {
                CollegeScheduleApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun CollegeScheduleApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.12:5237/") // localhost для Android Emulator
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api = remember { retrofit.create(ScheduleApi::class.java) }
    val repository = remember { ScheduleRepository(api) }
    var selectedGroup by rememberSaveable { mutableStateOf("") }
    var groups by remember { mutableStateOf<List<GroupDto>>(emptyList()) }
    var groupsLoaded by remember { mutableStateOf(false) }
    var errorGroups by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var favorites by remember { mutableStateOf<Set<String>>(emptySet()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        getFavoritesFlow(context).collectLatest { favorites = it }
    }

    LaunchedEffect(Unit) {
        try {
            groups = repository.getGroups()
            if (groups.isNotEmpty()) {
                selectedGroup = groups.find { it.groupName == "ИС-12" }?.groupName ?: groups[0].groupName
            }
        } catch (e: Exception) {
            errorGroups = e.message
        } finally {
            groupsLoaded = true
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> if (errorGroups != null) {
                    Text("Ошибка загрузки групп: $errorGroups", modifier = Modifier.padding(innerPadding))
                } else {
                    ScheduleScreen(
                        repository = repository,
                        selectedGroup = selectedGroup,
                        onSelectedGroupChange = { selectedGroup = it },
                        favorites = favorites,
                        groupsLoaded = groupsLoaded,
                        groups = groups
                    )
                }
                AppDestinations.FAVORITES -> FavoritesScreen(
                    favorites = favorites,
                    onGroupClick = { group ->
                        selectedGroup = group
                        currentDestination = AppDestinations.HOME
                    },
                    innerPadding = innerPadding
                )
                AppDestinations.PROFILE ->
                    Text("Профиль студента", modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}