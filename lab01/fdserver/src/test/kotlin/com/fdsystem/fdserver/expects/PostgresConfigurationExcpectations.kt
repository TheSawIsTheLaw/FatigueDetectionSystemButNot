package com.fdsystem.fdserver.expects

data class PostgresConfigurationExcpectations(
    val postgresURL: String = "postgres:5432/users",
    val postgresUsername: String = "admin",
    val postgresPassword: String = "satanIsHere"
)
