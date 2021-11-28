package com.fdsystem.fdserver.domain.charrepository

import com.fdsystem.fdserver.domain.logicentities.DSDataAccessInfo
import com.fdsystem.fdserver.domain.logicentities.DSDataAddInfo
import com.fdsystem.fdserver.domain.logicentities.DSMeasurement

interface CharRepositoryInterface
{
    fun get(dataAccessInfo: DSDataAccessInfo): List<DSMeasurement>

    fun add(dataAddInfo: DSDataAddInfo)
}