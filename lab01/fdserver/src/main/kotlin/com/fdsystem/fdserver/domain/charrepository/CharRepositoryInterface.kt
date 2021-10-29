package com.fdsystem.fdserver.domain.charrepository

import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurementList
import com.fdsystem.fdserver.domain.models.CRMeasurement

interface CharRepositoryInterface
{
    fun get(dataAccessInfo: DSDataAccessInfo): List<CRMeasurement>

    fun add(dataAddInfo: DSDataAddInfo)
}