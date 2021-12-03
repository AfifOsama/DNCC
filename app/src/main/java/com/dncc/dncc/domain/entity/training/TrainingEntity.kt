package com.dncc.dncc.domain.entity.training

data class TrainingEntity(
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingId: String = "",
    val trainingName: String = "",
    val meets: List<MeetEntity> = mutableListOf()
)
