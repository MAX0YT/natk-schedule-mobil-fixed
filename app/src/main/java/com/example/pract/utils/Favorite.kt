package com.example.pract.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

private val FAVORITES_KEY = stringSetPreferencesKey("favorites")

suspend fun addFavorite(context: Context, group: String) {
    context.dataStore.edit { prefs ->
        val current = prefs[FAVORITES_KEY] ?: emptySet()
        prefs[FAVORITES_KEY] = current + group
    }
}

suspend fun removeFavorite(context: Context, group: String) {
    context.dataStore.edit { prefs ->
        val current = prefs[FAVORITES_KEY] ?: emptySet()
        prefs[FAVORITES_KEY] = current - group
    }
}

fun getFavoritesFlow(context: Context): Flow<Set<String>> =
    context.dataStore.data.map { it[FAVORITES_KEY] ?: emptySet() }