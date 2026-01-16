package com.example.pract.data.repository

import com.example.pract.data.api.ScheduleApi
import com.example.pract.data.dto.ScheduleByDateDto
import com.example.pract.data.dto.GroupDto

class ScheduleRepository(private val api: ScheduleApi) {
    suspend fun loadSchedule(group: String, start: String, end: String): List<ScheduleByDateDto> {
        return api.getSchedule(
            groupName = group,
            start = start,
            end = end
        )
    }

    suspend fun getGroups(): List<GroupDto> {
        return api.getGroups()
    }
}
