package com.dncc.dncc.domain.entity.training

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrainingEntity(
    val trainingId: String = "",
    val desc: String = "",
    val linkWa: String = "",
    val mentor: String = "",
    val schedule: String = "",
    val trainingName: String = "",
    val participantMax: Int = 100,
    val participantNow: Int = 0
): Parcelable
