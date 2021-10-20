package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.data.CharRepositoryImpl
import com.fdsystem.fdserver.data.UserRepositoryImpl
import com.sun.net.httpserver.Authenticator
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BucketsService()
{
    private var repository: CharRepositoryImpl? = null
    private var userRepository = UserRepositoryImpl(NetworkConfig.postgresUsername, NetworkConfig.postgresPassword)

    fun loginToInflux(connectionString: String, token: String, org: String)
    {
        repository = CharRepositoryImpl(connectionString, token, org)
    }

    fun register(username: String, password: String): String
    {
        val newToken = userRepository.registerUser(username, password)

        if (newToken.isEmpty())
        {
            return "User already exists"
        }

        return newToken
    }

    fun login(username: String, password: String): String
    {
        val token = userRepository.getUserToken(username, password)
        if (token == "User doesn't exist" || token == "Wrong password")
        {
            return token
        }

        repository = CharRepositoryImpl(NetworkConfig.influxdbURL, token, NetworkConfig.influxOrganization)
        return "Success"
    }

    fun changeUserInfo(oldUsername: String, newUsername: String, oldPassword: String, newPassword: String): Boolean
    {
        logout()
        return userRepository.changePasswordAndUsername(oldUsername, newUsername, oldPassword, newPassword)
    }

    fun logout()
    {
        repository = null
    }

    fun checkDBHealth(): String
    {
        repository ?: return "Dead"
        return if (repository!!.checkHealth()) "Authorized" else "Error"
    }

    fun checkAuth(): Boolean
    {
        repository ?: return false
        return repository!!.checkHealth()
    }

    fun getMeasurementFromBucket(bucketName: String, charName: String): List<Pair<String, Instant>>
    {
        val outList = mutableListOf<Pair<String, Instant>>()
        if (repository != null)
        {
            val gotInformation = repository!!.get(bucketName, Pair(0, 0), charName)
            for (i in gotInformation)
            {
                outList.add(0, Pair(i.second.toString(), i.third))
            }
        }

        return outList.toList()
    }

    fun sendToBucket(bucketName: String, charName: String, chars: List<String>)
    {
        if (repository == null)
            return

        repository!!.add(bucketName, charName, chars)
    }
}