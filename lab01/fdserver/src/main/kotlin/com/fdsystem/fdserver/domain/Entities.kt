package com.fdsystem.fdserver.domain

import java.time.Instant

// TODO() Разбить файл

data class MeasurementDTO(
    val measurement: String,
    val value: String,
    val time: Instant
)

data class DataServiceMeasurement(
    val value: String,
    val time: Instant
)

data class DataServiceMeasurementValue(
    val value: String
)

data class DataServiceMeasurements(
    val measurement: String,
    val values: List<DataServiceMeasurementValue>
)

data class UserCredentials(
    val username: String, val password: String, val dbToken: String
)

data class UserCredentialsToAuth(
    val username: String, val password: String
)

data class PasswordChangeEntity(
    val oldPassword: String,
    val newPassword: String
)