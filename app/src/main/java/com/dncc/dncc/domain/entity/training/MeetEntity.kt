package com.dncc.dncc.domain.entity.training

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetEntity(
    val trainingId: String = "",
    val meetId: String = "",
    val description: String = "",
    val fileDownloadLink: String = "",
    val meetName: String = ""
): Parcelable
