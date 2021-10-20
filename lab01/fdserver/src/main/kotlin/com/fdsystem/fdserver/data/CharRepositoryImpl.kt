package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.charrepository.CharRepositoryInterface
import com.influxdb.client.domain.HealthCheck
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant

class InfluxConnection(connectionString_: String, token_: String, org_: String)
{
    private val connectionString = connectionString_
    private val token = token_
    private val org = org_
    private var connection = InfluxDBClientKotlinFactory
        .create(connectionString, token.toCharArray(), org)
    private var writeApiConnection: InfluxDBClientKotlin? = null

    fun getConnectionURL(): String
    {
        return connectionString
    }

    fun getToken(): String
    {
        return token
    }

    fun getOrg(): String
    {
        return org
    }

    fun getConnectionToDB(): InfluxDBClientKotlin
    {
//        if (connection.health().status.toString() == "fail")
//        {
//            connection = InfluxDBClientKotlinFactory
//                .create(connectionString, token.toCharArray(), org)
//        }
        return connection
    }

    fun getConnectionWrite(bucketName: String): InfluxDBClientKotlin
    {
//        if (writeApiConnection.health().status == HealthCheck.StatusEnum.FAIL)
//        {
        writeApiConnection?.close()

        writeApiConnection = InfluxDBClientKotlinFactory
            .create(connectionString, token.toCharArray(), org, bucketName)
//        }
        return writeApiConnection as InfluxDBClientKotlin
    }

    fun closeConnection()
    {
        connection.close()
    }

    fun closeWriteConnection()
    {
        writeApiConnection?.close()
    }
}

class CharRepositoryImpl(connectionString: String, token: String, org: String) : CharRepositoryInterface
{
    private val connection = InfluxConnection(connectionString, token, org)

    override fun get(
        subjectName: String, timeRange: Pair<Int, Int>,
        charName: String
    ): List<Triple<String, Any, Instant>>
    {
        if (connection.getConnectionToDB().health().status == HealthCheck.StatusEnum.FAIL) // Исправить говно какое-то
            return listOf()

        val outList: MutableList<Triple<String, Any, Instant>> = mutableListOf()
        val client = connection.getConnectionToDB()

        val rng =
            if (timeRange.second == 0) "start: ${timeRange.first}" else "start: ${timeRange.first}, stop: ${timeRange.second}}"
        var query: String = "from(bucket: \"$subjectName\")\n" +
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
                outList.add(
                    Triple(
                        curVal["_measurement"].toString(), curVal["_value"]!!,
                        curVal["_time"] as Instant
                    )
                )
            }
        }
        connection.closeConnection()

        return outList.toList()
    }

    private fun getOrgIDByName(apiString: String, orgName: String): String
    {
        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
            .addQueryParameter("org", orgName)
            .build()

        val request = Request.Builder()
            .url(urlWithParams)
            .addHeader("Authorization", "Token ${connection.getToken()}")
            .get()
            .build()

        val httpClient = OkHttpClient()
        val response = httpClient.newCall(request).execute()
        if (response.code() != 200)
        {
            throw Exception("Connection to database failed")
        }

        val retVal = response.body()!!.string()
        response.close()
        val regex = "\"orgID\": \"[a-z0-9]+\"".toRegex()
        val res = regex.find(retVal) ?: throw Exception("Org ID is not defined")
        return retVal.substring(res.range.first + 10, res.range.last)
    }

    private fun createBucket(subjectName: String)
    {
        val httpClient = OkHttpClient()
        var apiString = connection.getConnectionURL()
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/buckets"

        val orgID = getOrgIDByName(apiString, connection.getOrg())
        val jsonContent = "{\n" +
                "  \"orgID\": \"$orgID\",\n" +
                "  \"name\": \"$subjectName\",\n" +
                "  \"retentionRules\": []\n" +
                "}"
        val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), jsonContent)

        val request = Request.Builder()
            .url(apiString)
            .addHeader(
                "Authorization",
                "Token ${connection.getToken()}"
            )
            .post(body)
            .build()

        httpClient.newCall(request).execute()
    }

    private fun bucketNotExists(bucketName: String): Boolean
    {
        val httpClient = OkHttpClient()

        var apiString = connection.getConnectionURL()
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/buckets"

        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
            .addQueryParameter("name", bucketName)
            .build()

        val request = Request.Builder()
            .url(urlWithParams)
            .addHeader("Authorization", "Token ${connection.getToken()}")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()
        // ЗАКРЫВАТЬ RESPONSE
        // Или блок using??
        if (response.code() != 200)
        {
            throw Exception("Connection to database failed")
        }

        val retVal = response.body()!!.string().contains("\"buckets\": []")
        response.close()
        return retVal
    }

    override fun add(subjectName: String, charName: String, chars: List<String>)
    {
        if (bucketNotExists(subjectName))
        {
            createBucket(subjectName)
        }

        val client = connection.getConnectionWrite(subjectName)
        val writeApi = client.getWriteKotlinApi()

        runBlocking {
            for (i in chars)
            {
                writeApi.writeRecord("$charName value=$i", WritePrecision.S)
            }
        }

        connection.closeWriteConnection()
    }

    fun checkHealth(): Boolean
    {
        val httpClient = OkHttpClient()

        var apiString = connection.getConnectionURL()
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/buckets"

        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
            .build()

        val request = Request.Builder()
            .url(urlWithParams)
            .addHeader("Authorization", "Token ${connection.getToken()}")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()
        val retVal = response.body()!!.string().contains("\"buckets\":")
        response.close()

        return retVal
    }
}