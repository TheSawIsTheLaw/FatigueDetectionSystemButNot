package com.fdsystem.fdserver.domain

interface UserRepositoryInterface
{
    fun userExists(username: String)

    fun registerUser(username: String, password: String): Boolean

    fun getUserToken(username: String, password: String): String
}