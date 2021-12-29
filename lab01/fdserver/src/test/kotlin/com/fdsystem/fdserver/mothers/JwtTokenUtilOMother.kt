package com.fdsystem.fdserver.mothers

data class JwtTokenUtilOMother(
    val defaultUser: String = "user",
    val defaultPassword: String = "password",
    val defaultDBToken: String = "123",

    val invalidToken: String = "lol, i'm a token uwu",
    val invalidUsername: String = "Invalid",

    val defaultClaims: HashMap<String, String> = hashMapOf("dbToken" to "1222"),
    val defaultSecretKey: String = "LZKPDJMW7MSY273666MZNAAAUCJEBASUKOXK666777A"
)
