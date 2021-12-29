package com.fdsystem.fdserver.mothers

data class InfluxConnectionOMother(
    val defaultConnectionString: String = "http://localhost:8086",
    val nonParseableConnectionString: String = "u cannot parse me",

    val defaultToken: String = "tok",

    val defaultOrg: String = "org",

    val defaultBucketName: String = "someone"
)
