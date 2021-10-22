package com.fdsystem.fdserver.config

object NetworkConfig
{
    // InfluxDB necessary info
    val influxdbURL: String = "http://influxdb:8086"
    val influxAdminToken: String =
        "qG8iPh0Uz4aKw70lZQYzFoL0HL8oFuPuvfZQoDkMTj59c-efmnaTixWAkAT2pwtOyLYZFkGWgZPKSYFQX3-7fQ=="
    val influxOrganization: String = "subjects"

    // Postgres necessary info
    val postgresURL: String = "postgres:5432/users"
    val postgresUsername: String = "admin"
    val postgresPassword: String = "satanIsHere"
}
