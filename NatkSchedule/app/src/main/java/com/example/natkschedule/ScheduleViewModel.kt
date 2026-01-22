package com.example.natkschedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScheduleRepository()
    private val favoritesManager = FavoritesManager(application)

    private val _groups = MutableStateFlow<List<String>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredGroups = MutableStateFlow<List<String>>(emptyList())
    val filteredGroupsFlow: StateFlow<List<String>> = _filteredGroups

    private val _schedule = MutableStateFlow<List<ScheduleByDateDto>>(emptyList())
    val schedule: StateFlow<List<ScheduleByDateDto>> = _schedule

    private val _selectedGroup = MutableStateFlow("")
    val selectedGroup: StateFlow<String> = _selectedGroup

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    init {
        loadGroups()
        loadFavorites()
    }

    private fun loadFavorites() {
        _favorites.value = favoritesManager.getFavorites()
    }

    fun toggleFavorite(group: String) {
        if (favoritesManager.isFavorite(group)) {
            favoritesManager.removeFavorite(group)
        } else {
            favoritesManager.addFavorite(group)
        }
        loadFavorites()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            val loadedGroups = repository.getGroups()
            _groups.value = loadedGroups
            updateFilteredGroups()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateFilteredGroups()
    }

    private fun updateFilteredGroups() {
        val query = _searchQuery.value
        _filteredGroups.value = if (query.isBlank()) {
            _groups.value
        } else {
            _groups.value.filter { it.contains(query, ignoreCase = true) }
        }
    }

    fun selectGroup(group: String) {
        if (group.isBlank()) return
        _selectedGroup.value = group
        _searchQuery.value = group 
        // Hide dropdown logic will be in UI
        loadSchedule(group)
    }

    fun loadSchedule(group: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val start = dateFormat.format(calendar.time)
                
                calendar.add(Calendar.DAY_OF_YEAR, 30)
                val end = dateFormat.format(calendar.time)

                _schedule.value = repository.getSchedule(group, start, end)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
