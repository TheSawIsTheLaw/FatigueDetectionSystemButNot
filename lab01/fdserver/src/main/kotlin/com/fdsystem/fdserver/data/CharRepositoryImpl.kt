package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.CharRepositoryInterface
import com.fdsystem.fdserver.domain.Characteristics
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory

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

class CharRepositoryImpl(connectionString: String, token: String, org: String): CharRepositoryInterface
{
    private val connection = InfluxConnection(connectionString, token, org)

    override fun get(subjectName: String, timeRange: Pair<Int, Int>): List<Characteristics>
    {
        TODO("Not yet implemented")
    }

    override fun add(subjectName: String, chars: List<Characteristics>)
    {
        TODO("Not yet implemented")
    }
}