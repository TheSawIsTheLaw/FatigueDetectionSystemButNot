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

data class Measurement(
    val measurement: String,
    val values: List<String>
)

data class MeasurementsToSend(
    val measurements: List<Measurement>
)

data class UserCredentials(
    val username: String, val password: String, val dbToken: String
)

data class UserCredentialsToAuth(
    val username: String, val password: String
)

data class PasswordChangeInformation(
    val oldPassword: String,
    val newPassword: String
)

data class RequiredMeasurementsNames(
    val listOfMeasurementsNames: List<String>
)