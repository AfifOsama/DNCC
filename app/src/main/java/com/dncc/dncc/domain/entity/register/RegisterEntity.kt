package com.dncc.dncc.domain.entity.register

data class RegisterEntity(
    val email: String = "",
    val password: String = "",
    val photoPath: String = "",
    val fullName: String = "",
    val major: String = "",
    val nim: String = "",
    val noHp: String = ""
)
