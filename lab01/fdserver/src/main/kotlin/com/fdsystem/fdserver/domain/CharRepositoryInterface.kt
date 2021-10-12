package com.fdsystem.fdserver.domain

import java.time.Instant


interface CharRepositoryInterface
{
    fun get(subjectName: String, timeRange: Pair<Int, Int>, charName: String = ""): List<Triple<String, Any, Instant>>
    fun add(subjectName: String, charName: String, chars: List<String>)
}