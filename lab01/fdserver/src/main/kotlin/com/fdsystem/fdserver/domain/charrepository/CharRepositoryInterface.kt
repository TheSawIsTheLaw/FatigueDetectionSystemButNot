package com.fdsystem.fdserver.domain.charrepository

import com.fdsystem.fdserver.domain.dtos.MeasurementDTO

interface CharRepositoryInterface
{
    fun get(
        subjectName: String,
        timeRange: Pair<Int, Int>,
        charName: String = ""
    ): List<MeasurementDTO>

    fun add(subjectName: String, chars: List<MeasurementDTO>)
}