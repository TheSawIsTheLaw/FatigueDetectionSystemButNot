package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.CharRepositoryInterface
import com.google.gson.JsonObject
import com.influxdb.client.JSON
import com.influxdb.client.domain.HealthCheck
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import java.text.Format
import java.time.Instant

class InfluxConnection(connectionString_: String, token_: String, org_: String)
{
    private val connectionString = connectionString_
    private val token = token_
    private val org = org_
    private var connection: InfluxDBClientKotlin
    private lateinit var writeApiConnection: InfluxDBClientKotlin

    init
    {
        connection = InfluxDBClientKotlinFactory
            .create(connectionString, token.toCharArray(), org)
    }

    fun getConnectionURL(): String
    {
        return connectionString
    }

    fun getToken(): String
    {
        return token
    }

    fun getConnectionToDB(): InfluxDBClientKotlin
    {
        if (connection.health().status == HealthCheck.StatusEnum.FAIL)
        {
            connection = InfluxDBClientKotlinFactory
                .create(connectionString, token.toCharArray(), org)
        }
        return connection
    }

    fun getConnectionWrite(bucketName: String): InfluxDBClientKotlin
    {
        if (writeApiConnection.health().status == HealthCheck.StatusEnum.FAIL)
        {
            writeApiConnection = InfluxDBClientKotlinFactory
                .create(connectionString, token.toCharArray(), org, bucketName)
        }
        return writeApiConnection
    }

    fun closeConnection()
    {
        connection.close()
    }

    fun closeWriteConnection()
    {
        writeApiConnection.close()
    }
}

class CharRepositoryImpl(connectionString: String, token: String, org: String) : CharRepositoryInterface
{
    private val orgIDToAddUsers = "c51d6cb468ec609f"

    private val connection = InfluxConnection(connectionString, token, org)

    override fun get(subjectName: String, timeRange: Pair<Int, Int>,
                     charName: String): List<Triple<String, Any, Instant>>
    {
        if (connection.getConnectionToDB().health().status == HealthCheck.StatusEnum.FAIL)
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
                outList.add(Triple(curVal["_measurement"].toString(), curVal["_value"]!!,
                    curVal["_time"] as Instant))
            }
        }
        connection.closeConnection()

        return outList.toList()
    }

    private fun createBucket(subjectName: String)
    {
        val httpClient = OkHttpClient()

        val url = HttpUrl.parse("http://localhost:8086/api/v2/buckets")!!.newBuilder()
            .build()

        val jsonContent = "{\n" +
                "  \"orgID\": \"$orgIDToAddUsers\",\n" +
                "  \"name\": \"$subjectName\",\n" +
                "  \"retentionRules\": []\n" +
                "}"
        val body = okhttp3.RequestBody.create(okhttp3.MediaType.get("application/json; charset=utf8"), jsonContent)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization",
                "Token EymNpn7A_2aZHevNV_CCnttU0YGq93v3QxaV5dRSQ2E43cJK4r3MZIffvgYbeWDwtuTRSJHiP7WqWtBAuMhJOA==")
            .post(body)
            .build()

        httpClient.newCall(request).execute()
    }

    private fun bucketNotExists(bucketName: String): Boolean
    {
        val httpClient = OkHttpClient()

        var apiString = connection.getConnectionURL()
        if (apiString.last() != '/')
            apiString += '/'
        apiString += "api/v2/buckets"

        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
            .addQueryParameter("name", bucketName)
            .build()

        val request = Request.Builder()
            .url(urlWithParams)
            .get()
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.code() != 200)
        {
            throw Exception("Connection to database failed")
        }

        return response.body()!!.string().contains("\"buckets\": []")
    }

    override fun add(subjectName: String, chars: List<Pair<String, Any>>)
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
                writeApi.writeRecord("${i.first}=${i.second}", WritePrecision.S)
            }
        }
        
        connection.closeWriteConnection()
    }
}