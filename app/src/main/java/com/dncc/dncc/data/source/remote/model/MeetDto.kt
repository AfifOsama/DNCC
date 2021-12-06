package com.dncc.dncc.data.source.remote.model

import com.dncc.dncc.domain.entity.training.MeetEntity

data class MeetDto(
    val meetId: String = "",
    val description: String = "",
    val filePath: String = "",
    val meetName: String = ""
)

fun MeetDto.toMeetEntity(): MeetEntity {
    return MeetEntity(
        meetId = meetId,
        description = description,
        filePath = filePath,
        meetName = meetName
    )
}
