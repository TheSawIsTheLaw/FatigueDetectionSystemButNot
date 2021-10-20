package com.fdsystem.fdserver.config

object NetworkConfig
{
    // InfluxDB necessary info
    val influxdbURL: String = "localhost:8086"
    val influxAdminToken: String =
        "uZpHcWiQ3MG2RY5_nvowOD2QT5AYaIpOjtHl56v6bSU7bt_NhoKUFUUr0qoa3gpXdtfSjbNqfBhuXCJFxTwHEA=="
    val influxOrganization: String = "subjects"

    // Postgres necessary info
    val postgresURL: String = "localhost:5432"
    val postgresUsername: String = "admin"
    val postgresPassword: String = "satanIsHere"
}
