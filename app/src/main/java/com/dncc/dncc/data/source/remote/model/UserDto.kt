package com.dncc.dncc.data.source.remote.model

import com.dncc.dncc.domain.entity.user.UserEntity

data class UserDto(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val major: String = "",
    val nim: String = "",
    val noHp: String = "",
    val role: String = "",
    val training: String = "",
)

fun UserDto.toUserEntity(): UserEntity {
    return UserEntity(
        userId, email, fullName, major, nim, noHp, role, training
    )
}
