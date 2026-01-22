package com.example.natkschedule

import android.content.Context
import android.content.SharedPreferences

class FavoritesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    fun getFavorites(): Set<String> {
        return prefs.getStringSet("favorite_groups", emptySet()) ?: emptySet()
    }

    fun addFavorite(group: String) {
        val current = getFavorites().toMutableSet()
        current.add(group)
        prefs.edit().putStringSet("favorite_groups", current).apply()
    }

    fun removeFavorite(group: String) {
        val current = getFavorites().toMutableSet()
        current.remove(group)
        prefs.edit().putStringSet("favorite_groups", current).apply()
    }

    fun isFavorite(group: String): Boolean {
        return getFavorites().contains(group)
    }
}
