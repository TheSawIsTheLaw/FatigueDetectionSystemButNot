package com.fdsystem.fdserver.factories

import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo

class DSDataAccessInfoFactory {
    private val token = "HsJBf0sINtvxedXJio2Lg7iskJgLcR5q8a0MZtqoiWZt66pBEQ0LUz0IPEe5ooD2GqaxQoGxzqoIi-U1CLINow=="
    private val bucket = "testUser"

    fun createDSDataAccessInfoByMeasurementName(name: String) = DSDataAccessInfo(token, bucket, Pair(0, 0), name)
}