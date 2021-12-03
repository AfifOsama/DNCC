package com.dncc.dncc.data.source.remote.model

import com.dncc.dncc.domain.entity.training.MeetEntity

data class MeetDto(
    val description: String = "",
    val filePath: String = "",
    val idMeet: String = "",
    val meetName: String = ""
)

fun MeetDto.toMeetEntity(): MeetEntity {
    return MeetEntity(
        description = description,
        filePath = filePath,
        idMeet = idMeet,
        meetName = meetName
    )
}
