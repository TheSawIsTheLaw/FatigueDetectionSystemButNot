package com.fdsystem.fdserver.expects

data class InfluxdbConfigurationExpectations(
    val influxdbURL: String = "http://influxdb:8086",
    val influxdbOrganization: String = "subjects",
    val influxdbAdminToken: String = "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
)
