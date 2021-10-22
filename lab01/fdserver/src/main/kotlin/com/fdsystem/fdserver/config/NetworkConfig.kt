package com.fdsystem.fdserver.config

object NetworkConfig
{
    // InfluxDB necessary info
    val influxdbURL: String = "http://localhost:8086"
    val influxAdminToken: String =
        "xkwv7-sIYO1IS0vlQB0HeJGp8vqcC_39WKDvTcnLMd8ZRHaJ469yXH1-7OW-56mrMY-mR7TTVDZYOJ8oqAw-Jg=="
    val influxOrganization: String = "subjects"

    // Postgres necessary info
    val postgresURL: String = "localhost:5432"
    val postgresUsername: String = "admin"
    val postgresPassword: String = "satanIsHere"
}
