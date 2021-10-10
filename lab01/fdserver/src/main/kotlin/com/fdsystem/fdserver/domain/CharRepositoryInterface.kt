package com.fdsystem.fdserver.domain


interface CharRepositoryInterface
{
    fun get(subjectName: String, timeRange: Pair<Int, Int>): List<Characteristics>
    fun add(subjectName: String, chars: List<Characteristics>)
}