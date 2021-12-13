package com.dncc.dncc.data.source.remote.model

import com.dncc.dncc.domain.entity.training.TrainingEntity

data class TrainingDto(
    val trainingId: String = "",
    val desc: String = "",
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingName: String = "",
    val participantMax: Int = 100,
    val participantNow: Int = 0
)

fun TrainingDto.toTrainingEntity(): TrainingEntity {
    return TrainingEntity(
        trainingId = trainingId,
        desc = desc,
        linkWa = linkWa,
        mentor = mentor,
        schedule = schedule,
        trainingName = trainingName,
        participantMax = participantMax,
        participantNow = participantNow
    )
}
