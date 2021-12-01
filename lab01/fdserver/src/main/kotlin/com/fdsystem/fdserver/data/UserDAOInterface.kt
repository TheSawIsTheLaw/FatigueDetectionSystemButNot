package com.fdsystem.fdserver.data

interface UserDAOInterface {
    fun registerUser(username: String, password: String, token: String)

    fun getUserByUsername(username: String): Triple<String, String, String>

    fun changeCredentials(oldUsername: String, newUsername: String, oldPassword: String, newPassword: String)
}