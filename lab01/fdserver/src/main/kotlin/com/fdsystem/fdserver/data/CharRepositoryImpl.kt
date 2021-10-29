package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.NetworkConfig
import com.fdsystem.fdserver.domain.dtos.MeasurementDTO
import com.fdsystem.fdserver.domain.charrepository.CharRepositoryInterface
import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import com.fdsystem.fdserver.domain.logicentities.DSUserCredentials
import com.fdsystem.fdserver.domain.models.CRMeasurement
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
    val connectionString = connectionString_
    val token = token_
    val org = org_

    fun getConnectionToDB(): InfluxDBClientKotlin
    {
        return InfluxDBClientKotlinFactory.create(
            connectionString, token
                .toCharArray(), org
        )
    }

    fun getConnectionWrite(bucketName: String): InfluxDBClientKotlin
    {
        return InfluxDBClientKotlinFactory
            .create(
                connectionString,
                token.toCharArray(),
                org,
                bucketName
            )
    }
}

class CharRepositoryImpl(connectionString: String, token: String, org: String) :
    CharRepositoryInterface
{
    private val connection = InfluxConnection(connectionString, token, org)

    override fun get(dataAccessInfo: DSDataAccessInfo): List<CRMeasurement>
    {
        val timeRange = dataAccessInfo.timeRange
        val measurement = dataAccessInfo.measurementName
        val bucket = dataAccessInfo.bucketName

        val outList: MutableList<CRMeasurement> = mutableListOf()
        connection.getConnectionToDB().use {
            var rng = "start: ${timeRange.first}"
            if (timeRange.second != 0)
            {
                rng += ", stop: ${timeRange.second}}"
            }

            var query: String = "from(bucket: \"$bucket\")\n" +
                    "|> range($rng)"
            if (measurement.isNotBlank())
            {
                query += "\n|> filter(fn: (r) => (r[\"_measurement\"] == " +
                        "\"$measurement\"))"
            }
            val result = it.getQueryKotlinApi().query(query)

            runBlocking {
                for (i in result)
                {
                    val curVal = i.values
                    outList.add(
                        CRMeasurement(
                            curVal["_measurement"].toString(),
                            curVal["_value"].toString(),
                            curVal["_time"] as Instant
                        )
                    )
                }
            }
        }

        return outList.toList()
    }

    private fun getOrgIDByName(apiString: String, orgName: String): String
    {
        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
            .addQueryParameter("org", orgName)
            .build()

        val request = Request.Builder()
            .url(urlWithParams)
            .addHeader("Authorization", "Token ${connection.token}")
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
        var apiString = connection.connectionString
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/buckets"

        val orgID = getOrgIDByName(apiString, connection.org)
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
                "Token ${connection.token}"
            )
            .post(body)
            .build()

        httpClient.newCall(request).execute()
    }

    private fun bucketNotExists(bucketName: String): Boolean
    {
        val httpClient = OkHttpClient()

        var apiString = connection.connectionString
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
            .addHeader("Authorization", "Token ${connection.token}")
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

    override fun add(dataAddInfo: DSDataAddInfo)
    {
        val bucket = dataAddInfo.bucket
        val measurementList = dataAddInfo.measurementList

        if (bucketNotExists(bucket))
        {
            createBucket(bucket)
        }

        connection.getConnectionWrite(bucket).use {
            val writeApi = it.getWriteKotlinApi()

            runBlocking {
                val name = measurementList.name
                for (i in measurementList.measurements)
                {
                    writeApi.writeRecord(
                        "$name $name=${i.value}",
                        WritePrecision.S
                    )
                }
            }
        }
    }

    fun getNewTokenForUser(user: DSUserCredentials): String
    {
        val httpClient = OkHttpClient()
        var apiString = connection.connectionString
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/authorizations"

        val orgID = getOrgIDByName(apiString, connection.org)
        val jsonContent = "{\n" +
                "            \"description\": \"${user.username} token\",\n" +
                "            \"status\": \"active\",\n" +
                "            \"orgID\": \"$orgID\",\n" +
                "            \"permissions\": [\n" +
                "            {\n" +
                "                \"action\": \"read\",\n" +
                "                \"resource\": {\n" +
                "                \"type\": \"buckets\"\n" +
                "            }\n" +
                "            },\n" +
                "            {\n" +
                "                \"action\": \"write\",\n" +
                "                \"resource\": {\n" +
                "                \"type\": \"buckets\"\n" +
                "            }\n" +
                "            }\n" +
                "            ]\n" +
                "        }"


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
}

//    fun checkHealth(): Boolean
//    {
//        val httpClient = OkHttpClient()
//
//        var apiString = connection.getConnectionURL()
//        if (apiString.last() != '/')
//        {
//            apiString += '/'
//        }
//        apiString += "api/v2/buckets"
//
//        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
//            .build()
//
//        val request = Request.Builder()
//            .url(urlWithParams)
//            .addHeader("Authorization", "Token ${connection.getToken()}")
//            .get()
//            .build()
//
//        val retVal: Boolean
//        val response = httpClient.newCall(request).execute()
//        response.use {
//            retVal = response.body()!!.string().contains("\"buckets\":")
//        }
//
//        return retVal
//    }