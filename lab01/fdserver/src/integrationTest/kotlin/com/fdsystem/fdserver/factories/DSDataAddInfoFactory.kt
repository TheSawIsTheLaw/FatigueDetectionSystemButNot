package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import java.time.Instant

class DSDataAddInfoFactory {
    private val token = "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    private val bucket = "testUser"

    fun createDataAddInfoWithNewPulseZero(): DSDataAddInfo {
        return DSDataAddInfo(
            token,
            bucket,
            DSMeasurementList("pulse", listOf(DSMeasurement("pulse", "0.0", Instant.MAX)))
        )
    }
}