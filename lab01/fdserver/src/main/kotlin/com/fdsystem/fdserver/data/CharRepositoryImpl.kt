package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.config.InfluxdbConfiguration
import com.fdsystem.fdserver.domain.charrepository.CharRepositoryInterface
import com.fdsystem.fdserver.domain.logicentities.*
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Repository
import java.time.Instant

open class InfluxConnection(
    connectionString_: String, token_: String, org_: String
)
{
    private val connectionString = connectionString_
    private val token = token_
    private val org = org_

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

@Repository
class CharRepositoryImpl(private val config: InfluxdbConfiguration) :
    CharRepositoryInterface
{
    private fun get(
        dataAccessInfo: DSDataAccessInfo,
        connection: InfluxConnection
    ): List<DSMeasurement>
    {
        val timeRange = dataAccessInfo.timeRange
        val measurement = dataAccessInfo.measurementName
        val bucket = dataAccessInfo.bucketName

        val outList: MutableList<DSMeasurement> = mutableListOf()

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

        connection.getConnectionToDB().use {
            val result = it.getQueryKotlinApi().query(query)

            runBlocking {
                for (i in result)
                {
                    val curVal = i.values
                    outList.add(
                        DSMeasurement(
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

    override fun get(dataAccessInfo: DSDataAccessInfo): List<DSMeasurement>
    {
        return get(
            dataAccessInfo, InfluxConnection(
                config.configData.influxdbURL,
                dataAccessInfo.token,
                config.configData.influxdbOrganization
            )
        )
    }

    private fun getOrgIDByName(apiString: String, orgName: String): String
    {
        val urlWithParams = HttpUrl.parse(apiString)!!.newBuilder()
            .addQueryParameter("org", orgName)
            .build()

        val request = Request.Builder()
            .url(urlWithParams)
            .addHeader(
                "Authorization",
                "Token ${config.configData.influxdbAdminToken}"
            )
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
        var apiString = config.configData.influxdbURL
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/buckets"

        val orgID =
            getOrgIDByName(apiString, config.configData.influxdbOrganization)
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
                "Token ${config.configData.influxdbAdminToken}"
            )
            .post(body)
            .build()

        httpClient.newCall(request).execute()
    }

    private fun bucketNotExists(bucketName: String): Boolean
    {
        val httpClient = OkHttpClient()

        var apiString = config.configData.influxdbURL
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
            .addHeader(
                "Authorization",
                "Token ${config.configData.influxdbAdminToken}"
            )
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

    private fun add(dataAddInfo: DSDataAddInfo, connection: InfluxConnection)
    {
        val bucket = dataAddInfo.bucket
        val measurementList = dataAddInfo.measurementList

        if (bucketNotExists(bucket))
        {
            createBucket(bucket)
        }

        connection.getConnectionWrite(bucket).use {
            val writeApi = it.getWriteKotlinApi()

            val name = measurementList.name
            runBlocking {
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

    override fun add(dataAddInfo: DSDataAddInfo)
    {
        add(
            dataAddInfo, InfluxConnection(
                config.configData.influxdbURL,
                dataAddInfo.token,
                config.configData.influxdbOrganization
            )
        )
    }

    fun getNewTokenForUser(user: USUserCredentials): TokenInformation
    {
        val httpClient = OkHttpClient()
        var apiString = config.configData.influxdbURL
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/authorizations"

        val orgID =
            getOrgIDByName(apiString, config.configData.influxdbOrganization)
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
                "Token ${config.configData.influxdbAdminToken}"
            )
            .post(body)
            .build()

        var outBody: String

        val response = httpClient.newCall(request).execute()
        response.use { outBody = response.body()!!.string() }

        var regex = "\"token\": \"[^\"]*\"".toRegex()
        var regRes = regex.find(outBody) ?: throw Exception("Token parse error")

        val newToken =
            outBody.substring(regRes.range.first + 10, regRes.range.last)

        regex = "\"id\": \"[^\"]*\"".toRegex()
        regRes = regex.find(outBody) ?: throw Exception("ID parse error")
        val tokenId =
            outBody.substring(regRes.range.first + 7, regRes.range.last)

        return TokenInformation(newToken, tokenId)
    }

    fun deleteToken(tokenToDelete: TokenInformation): Boolean
    {
        val httpClient = OkHttpClient()
        var apiString = config.configData.influxdbURL
        if (apiString.last() != '/')
        {
            apiString += '/'
        }
        apiString += "api/v2/authorizations/${tokenToDelete.tokenID}"

        val request = Request.Builder()
            .url(apiString)
            .addHeader(
                "Authorization",
                "Token ${config.configData.influxdbAdminToken}"
            )
            .delete()
            .build()

        return (httpClient.newCall(request).execute().code() != 204)
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