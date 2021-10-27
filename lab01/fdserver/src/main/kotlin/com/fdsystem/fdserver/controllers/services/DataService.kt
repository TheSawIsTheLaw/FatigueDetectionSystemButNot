package com.fdsystem.fdserver.controllers.services

import com.fdsystem.fdserver.data.CharRepositoryImpl
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class DataService
{
    private var charRepository: CharRepositoryImpl? = null

    fun loginToInflux(connectionString: String, token: String, org: String)
    {
        charRepository = CharRepositoryImpl(connectionString, token, org)
    }

    fun setCharRepository(repository: CharRepositoryImpl?)
    {
        charRepository = repository
    }

    fun repositoryIsNull(): Boolean
    {
        return charRepository == null
    }

    fun checkHealth(): String
    {
        charRepository ?: return "Dead"
        return if (charRepository!!.checkHealth()) "Authorized" else "Error"
    }

    fun getMeasurement(
        bucketName: String,
        charName: String
    ): List<Pair<String, Instant>>
    {
        val outList = mutableListOf<Pair<String, Instant>>()
        if (charRepository != null)
        {
            val gotInformation =
                charRepository!!.get(bucketName, Pair(0, 0), charName)
            for (i in gotInformation)
            {
                outList.add(0, Pair(i.second.toString(), i.third))
            }
        }

        return outList.toList()
    }

    fun sendMeasurement(
        bucketName: String,
        charName: String,
        chars: List<String>
    )
    {
        charRepository?.add(bucketName, charName, chars)
    }
}