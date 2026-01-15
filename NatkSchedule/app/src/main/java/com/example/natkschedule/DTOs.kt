package com.example.natkschedule

import com.google.gson.annotations.SerializedName

data class LessonPartDto(
    @SerializedName("subjectName") val subjectName: String,
    @SerializedName("teacherName") val teacherName: String,
    @SerializedName("classroomNumber") val classroomNumber: String,
    @SerializedName("buildingName") val buildingName: String
)

data class LessonDto(
    @SerializedName("lessonNumber") val lessonNumber: Int,
    @SerializedName("timeStart") val timeStart: String,
    @SerializedName("timeEnd") val timeEnd: String,
    @SerializedName("parts") val parts: Map<String, LessonPartDto>
)

data class ScheduleByDateDto(
    @SerializedName("date") val date: String,
    @SerializedName("weekdayName") val weekdayName: String,
    @SerializedName("lessons") val lessons: List<LessonDto>
)
