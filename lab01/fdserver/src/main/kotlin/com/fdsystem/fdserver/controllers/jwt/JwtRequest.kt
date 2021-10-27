package com.fdsystem.fdserver.controllers.jwt

import java.io.Serializable


class JwtRequest : Serializable {
    private val serialVersionUID = 5926468583005150707L

    var username = ""
    var password = ""

    //need default constructor for JSON Parsing
    constructor() {}
    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }
}