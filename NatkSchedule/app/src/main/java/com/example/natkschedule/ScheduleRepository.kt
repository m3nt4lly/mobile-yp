package com.example.natkschedule

class ScheduleRepository {
    private val api = RetrofitInstance.api

    suspend fun getGroups(): List<String> {
        return try {
            api.getGroups()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSchedule(groupName: String, start: String, end: String): List<ScheduleByDateDto> {
        return try {
            api.getSchedule(groupName, start, end)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
