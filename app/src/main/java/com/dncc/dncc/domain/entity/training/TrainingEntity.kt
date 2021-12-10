package com.dncc.dncc.domain.entity.training

data class TrainingEntity(
    val trainingId: String = "",
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingName: String = "",
    val participantMax: Int = 100,
    val participantNow: Int = 0
)
