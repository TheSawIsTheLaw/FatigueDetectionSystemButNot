package com.fdsystem.fdserver.config

object NetworkConfig
{
    // InfluxDB necessary info
    val influxdbURL: String = "http://influxdb:8086"
    val influxAdminToken: String =
        "S2BxreP0buWYzaFZ3H6wXMdu1zuJddAon_fJzjFtq64oftC4_YaqhHlWLGobjD5n9D2dBfO3NCFAoV0EchXPOA=="
    val influxOrganization: String = "subjects"

    // Postgres necessary info
    val postgresURL: String = "postgres:5432/users"
    val postgresUsername: String = "admin"
    val postgresPassword: String = "satanIsHere"
}
