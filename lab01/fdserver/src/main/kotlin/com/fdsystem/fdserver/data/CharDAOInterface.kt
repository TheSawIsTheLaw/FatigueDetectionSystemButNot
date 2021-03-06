package com.fdsystem.fdserver.data

import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import com.influxdb.query.FluxRecord
import kotlinx.coroutines.channels.Channel

interface CharDAOInterface {
    fun get(
        connection: InfluxConnection,
        timeRange: Pair<Int, Int>,
        bucket: String,
        measurement: String
    ): List<DSMeasurement>

    fun add(connection: InfluxConnection, bucket: String, measurementList: DSMeasurementList)
}