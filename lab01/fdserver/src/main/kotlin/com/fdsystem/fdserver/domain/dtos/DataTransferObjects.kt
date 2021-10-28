package com.fdsystem.fdserver.domain.dtos

import java.time.Instant

data class MeasurementDTO(
    val measurement: String,
    val value: String,
    val time: Instant
)

data class PasswordChangeDTO(
    val oldUsername: String,
    val newUsername: String,
    val oldPassword: String,
    val newPassword: String
)

data class UserCredentialsDTO(
    val username: String,
    val password: String,
    val dbToken: String
)