package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.CharRepositoryInterface
import com.fdsystem.fdserver.domain.Characteristics
import com.influxdb.client.domain.HealthCheck
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.time.Instant

class InfluxConnection(connectionString_: String, token_: String, org_: String)
{
    private val connectionString = connectionString_
    private val token = token_
    private val org = org_
    private var connection: InfluxDBClientKotlin

    init
    {
        connection = InfluxDBClientKotlinFactory
            .create(connectionString, token.toCharArray(), org)
    }

    fun getConnectionToDB(): InfluxDBClientKotlin
    {
        connection = InfluxDBClientKotlinFactory
            .create(connectionString, token.toCharArray(), org)
        return connection
    }

    fun closeConnection()
    {
        connection.close()
    }
}

class CharRepositoryImpl(connectionString: String, token: String, org: String) : CharRepositoryInterface
{
    private val connection = InfluxConnection(connectionString, token, org)

    override fun get(subjectName: String, timeRange: Pair<Int, Int>, charName: String): List<Triple<String, String, Instant>>
    {
        if (connection.getConnectionToDB().health().status == HealthCheck.StatusEnum.FAIL)
            return listOf()

        val outList: MutableList<Triple<String, String, Instant>> = mutableListOf()
        val client = connection.getConnectionToDB()

        val rng =
            if (timeRange.second == 0) "start: ${timeRange.first}" else "start: ${timeRange.first}, stop: ${timeRange.second}}"
        var query: String  = "from(bucket: \"$subjectName\")\n" +
                "|> range($rng)"
        if (charName.isNotBlank())
        {
            query += "\n|> filter(fn: (r) => (r[\"_measurement\"] == \"$charName\"))"
        }
        val result = client.getQueryKotlinApi().query(query)

        runBlocking {
            for (i in result)
            {
                val curVal = i.values
                outList.add(Triple(curVal["_measurement"].toString(), curVal["_value"].toString(),
                    curVal["_time"] as Instant))
            }
        }
        connection.closeConnection()

        return outList.toList()
    }

    override fun add(subjectName: String, chars: List<Triple<String, String, Instant>>)
    {
        TODO("Not yet implemented")
    }
}