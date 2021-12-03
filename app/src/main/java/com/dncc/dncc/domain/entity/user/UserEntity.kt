package com.dncc.dncc.domain.entity.user

data class UserEntity(
    val userId: String = "",
    val photoPath: String = "",
    val email: String = "",
    val fullName: String = "",
    val major: String = "",
    val nim: String = "",
    val noHp: String = "",
    val role: String = "",
    val training: String = "",
)
