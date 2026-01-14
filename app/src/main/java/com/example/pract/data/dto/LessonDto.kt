package com.example.pract.data.dto
data class LessonDto(
    val lessonNumber: Int,
    val time: String,
    val groupParts: Map<LessonGroupPart, LessonPartDto?>
)