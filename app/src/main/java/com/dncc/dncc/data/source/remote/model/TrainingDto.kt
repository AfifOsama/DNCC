package com.dncc.dncc.data.source.remote.model

import com.dncc.dncc.domain.entity.training.TrainingEntity

data class TrainingDto(
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingId: String = "",
    val trainingName: String = "",
    val meets: List<MeetDto> = mutableListOf()
)

fun TrainingDto.toTrainingEntity(): TrainingEntity {
    return TrainingEntity(
        linkWa, mentor, schedule, trainingId, trainingName
    )
}
