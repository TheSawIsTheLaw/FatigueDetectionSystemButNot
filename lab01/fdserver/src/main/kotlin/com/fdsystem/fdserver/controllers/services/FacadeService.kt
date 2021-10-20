package com.fdsystem.fdserver.controllers.services

import org.springframework.stereotype.Service
import java.time.Instant

@Service
class FacadeService()
{
    private val dataService = DataService()
    private val userAuthService = UserAuthService()

    fun loginToInflux(connectionString: String, token: String, org: String)
    {
        dataService.loginToInflux(connectionString, token, org)
    }

    fun register(username: String, password: String): String
    {
        return userAuthService.register(username, password)
    }

    fun login(username: String, password: String): String
    {
        dataService.setCharRepository(userAuthService.login(username, password))

        return if (dataService.repositoryIsNull())
        {
            "Error"
        }
        else
        {
            "Success"
        }
    }

    fun changeUserInfo(oldUsername: String, newUsername: String, oldPassword: String, newPassword: String): Boolean
    {
        return userAuthService.changeUserInfo(oldUsername, newUsername, oldPassword, newPassword)
    }

    fun logout()
    {
        dataService.setCharRepository(null)
    }

    fun checkDBHealth(): String
    {
        return dataService.checkHealth()
    }

    fun checkAuth(): Boolean
    {
        return dataService.checkHealth() == "Authorized"
    }

    fun getMeasurementFromBucket(bucketName: String, charName: String): List<Pair<String, Instant>>
    {
        return dataService.getMeasurement(bucketName, charName)
    }

    fun sendToBucket(bucketName: String, charName: String, chars: List<String>)
    {
        dataService.sendMeasurement(bucketName, charName, chars)
    }
}