package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.logicentities.USUserCredentials

class USUserCredentialsFactory {
    fun createAnyWithoutToken() = USUserCredentials("newUser", "password", "")
}