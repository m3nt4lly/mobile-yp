package com.example.natkschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleViewModel : ViewModel() {
    private val repository = ScheduleRepository()

    private val _groups = MutableStateFlow<List<String>>(emptyList())
    val groups: StateFlow<List<String>> = _groups

    private val _schedule = MutableStateFlow<List<ScheduleByDateDto>>(emptyList())
    val schedule: StateFlow<List<ScheduleByDateDto>> = _schedule

    private val _selectedGroup = MutableStateFlow("")
    val selectedGroup: StateFlow<String> = _selectedGroup

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _groups.value = repository.getGroups()
        }
    }

    fun selectGroup(group: String) {
        println("Selected group: $group")
        _selectedGroup.value = group
        loadSchedule(group)
    }

    fun loadSchedule(group: String) {
        if (group.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Берем расписание на 30 дней вперед
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val start = dateFormat.format(calendar.time)
                
                calendar.add(Calendar.DAY_OF_YEAR, 30)
                val end = dateFormat.format(calendar.time)

                println("Fetching schedule for $group from $start to $end")
                val result = repository.getSchedule(group, start, end)
                println("Received ${result.size} days of schedule")
                _schedule.value = result
            } catch (e: Exception) {
                println("Error loading schedule: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
