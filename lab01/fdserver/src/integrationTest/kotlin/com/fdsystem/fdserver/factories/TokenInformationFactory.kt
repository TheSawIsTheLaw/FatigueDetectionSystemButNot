package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.logicentities.TokenInformation

class TokenInformationFactory {
    fun createNonExistingTokenInformation() = TokenInformation("lol", "1123")
}