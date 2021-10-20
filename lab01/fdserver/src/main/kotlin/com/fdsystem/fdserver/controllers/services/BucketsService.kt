package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BucketsService()
{
    private var repository: CharRepositoryImpl? = null

    fun loginToInflux(connectionString: String, token: String, org: String)
    {
        repository = CharRepositoryImpl(connectionString, token, org)
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

    fun logout()
    {
        repository = null
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