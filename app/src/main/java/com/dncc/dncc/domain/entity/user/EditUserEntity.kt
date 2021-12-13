package com.dncc.dncc.domain.entity.user

data class EditUserEntity(
    val userEntity: UserEntity = UserEntity(),
    val pathImage: String = "",
)