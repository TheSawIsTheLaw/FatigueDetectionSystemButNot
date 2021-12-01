package com.fdsystem.fdserver.expects.mocks

import com.fdsystem.fdserver.domain.logicentities.TokenInformation

data class UserAuthServiceMocksExpectations(
    private val successTokenGenerated: String = "token",
    private val failTokenGenerated: String = "failToken",

    private val defaultTokenID: String = "30",

    val successTokenInformation: TokenInformation = TokenInformation(successTokenGenerated, defaultTokenID),
    val failTokenInformation: TokenInformation = TokenInformation(failTokenGenerated, defaultTokenID)
)
