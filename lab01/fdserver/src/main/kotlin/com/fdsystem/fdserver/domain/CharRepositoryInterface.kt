package com.fdsystem.fdserver.domain

import java.time.Instant


interface CharRepositoryInterface
{
    fun get(subjectName: String, timeRange: Pair<Int, Int>): List<Triple<String, String, Instant>>
    fun get(subjectName: String, charName: String, timeRange: Pair<Int, Int>): List<Triple<String, String, Instant>>
    fun add(subjectName: String, chars: List<Triple<String, String, Instant>>)
}