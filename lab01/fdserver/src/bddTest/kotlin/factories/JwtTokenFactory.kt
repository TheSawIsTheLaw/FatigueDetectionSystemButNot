package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.logicentities.USUserCredentials
import org.springframework.security.core.userdetails.User

class JwtTokenFactory {
    private val jwtTokenUtil = JwtTokenUtilFactory().createJwtTokenUtilWithDefaultSecret()

    fun createTokenFromUser(credentials: USUserCredentials): String {
        return "Bearer " + jwtTokenUtil.generateToken(
            User(credentials.username, credentials.password, arrayListOf()),
            credentials.dbToken
        )
    }
}