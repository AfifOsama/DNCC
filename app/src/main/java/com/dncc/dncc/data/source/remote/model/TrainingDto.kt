package com.dncc.dncc.data.source.remote.model

import com.dncc.dncc.domain.entity.training.TrainingEntity

data class TrainingDto(
    val trainingId: String = "",
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingName: String = ""
)

fun TrainingDto.toTrainingEntity(): TrainingEntity {
    return TrainingEntity(
        trainingId, linkWa, mentor, schedule, trainingName
    )
}
