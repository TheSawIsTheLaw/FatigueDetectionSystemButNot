package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.NetworkConfig
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

class CharRepositoryImpl(connectionString: String, token: String, org: String) :
    CharRepositoryInterface
{
    private val connection = InfluxConnection(connectionString, token, org)

    override fun get(
        subjectName: String, timeRange: Pair<Int, Int>,
        charName: String
    ): List<Triple<String, Any, Instant>>
    {
        if (connection.getConnectionToDB()
                .health().status == HealthCheck.StatusEnum.FAIL
        ) // Исправить говно какое-то
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
        val retVal: String
        val response = httpClient.newCall(request).execute()
        response.use {
            if (response.code() != 200)
            {
                throw Exception("Connection to database failed")
            }

            retVal = response.body()!!.string()
        }

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
        val body = okhttp3.RequestBody.create(
            okhttp3.MediaType.parse("application/json"),
            jsonContent
        )

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

        val retVal: Boolean
        val response = httpClient.newCall(request).execute()
        response.use {
            if (response.code() != 200)
            {
                throw Exception("Connection to database failed")
            }
            retVal = response.body()!!.string().contains("\"buckets\": []")
        }

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

        val retVal: Boolean
        val response = httpClient.newCall(request).execute()
        response.use {
            retVal = response.body()!!.string().contains("\"buckets\":")
        }

        return retVal
    }

    fun getNewTokenForUser(username: String): String
    {
        val httpClient = OkHttpClient()
        var apiString = connection.getConnectionURL()
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/authorizations"

        val orgID = getOrgIDByName(apiString, connection.getOrg())
        // Тут требуется прописать все риды и райты + айдишники организации, походу
        val jsonContent = "{\n" +
                "  \"status\": \"active\",\n" +
                "  \"description\": \"$username token\",\n" +
                "  \"orgID\": \"$orgID\",\n" +
                "  \"permissions\": [\n" +
                "    {\n" +
                "      \"action\": \"write\",\n" +
                "      \"resource\": {\n" +
                "        \"type\": \"buckets\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}"

        val body = okhttp3.RequestBody.create(
            okhttp3.MediaType.parse("application/json"),
            jsonContent
        )

        val request = Request.Builder()
            .url(apiString)
            .addHeader(
                "Authorization",
                "Token ${NetworkConfig.influxAdminToken}"
            )
            .post(body)
            .build()

        var outBody: String

        val response = httpClient.newCall(request).execute()
        response.use { outBody = response.body()!!.string() }

        val regex = "\"token\": \"[^\"]*\"".toRegex()
        val regRes = regex.find(outBody)
            ?: throw java.lang.Exception("Token was not created")
        return outBody.substring(regRes.range.first + 10, regRes.range.last)
    }

//??? Может понадобиться. Если понадобится - изменим метод и внесём туда "mode" с тем, что потребуется делать
//    Только тут структура запроса неправильная
//    fun getNewTokenForSender(username: String): String {
//        val httpClient = OkHttpClient()
//        var apiString = connection.getConnectionURL()
//        if (apiString.last() != '/') {
//            apiString += '/'
//        }
//        apiString += "api/v2/authorizations"
//
//        val orgID = getOrgIDByName(apiString, connection.getOrg())
//        val jsonContent = "{\n" +
//                "  \"description\": \"$username token\",\n" +
//                "  \"orgID\": \"$orgID\",\n" +
//                "  \"permissions\": [\n" +
//                "    {\n" +
//                "      \"action\": \"write\",\n" +
//                "      \"resource\": {\n" +
//                "        \"type\": \"buckets\",\n"
//        "      }\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}"
//        val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), jsonContent)
//
//        val request = Request.Builder()
//            .url(apiString)
//            .addHeader(
//                "Authorization",
//                "Token ${NetworkConfig.influxAdminToken}"
//            )
//            .post(body)
//            .build()
//
//        var outBody: String
//
//        val response = httpClient.newCall(request).execute()
//        response.use { outBody = response.body()!!.string() }
//
//        val regex = "\"token\": \"[a-z0-9]+\"".toRegex()
//        val regRes = regex.find(outBody) ?: throw java.lang.Exception("Token was not created")
//        return outBody.substring(regRes.range.first + 10, regRes.range.last)
//    }
}