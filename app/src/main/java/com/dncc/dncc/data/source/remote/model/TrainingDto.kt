package com.dncc.dncc.data.source.remote.model

data class TrainingDto(
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingId: String = "",
    val trainingName: String = "",
    val meets: List<MeetDto> = mutableListOf()
)
