package com.example.foodrecipe.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private object Keys {
        val DIET = stringPreferencesKey("diet")
        val INTOLERANCES = stringPreferencesKey("intolerances")
    }

    val dietFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.DIET]?.takeIf { it.isNotBlank() }
    }

    val intolerancesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.INTOLERANCES] ?: ""
        if (raw.isBlank()) emptySet()
        else raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    suspend fun setDiet(value: String?) {
        context.dataStore.edit { prefs ->
            if (value.isNullOrBlank()) prefs.remove(Keys.DIET) else prefs[Keys.DIET] = value
        }
    }

    suspend fun setIntolerances(values: Set<String>) {
        context.dataStore.edit { prefs ->
            if (values.isEmpty()) prefs.remove(Keys.INTOLERANCES)
            else prefs[Keys.INTOLERANCES] = values.joinToString(",")
        }
    }

    fun getDietBlocking(): String? = runBlocking { dietFlow.first() }

    fun getIntolerancesBlocking(): Set<String> = runBlocking { intolerancesFlow.first() }
}
