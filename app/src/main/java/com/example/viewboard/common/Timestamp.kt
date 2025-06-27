package com.example.viewboard.common

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Timestamp (
    dateFmt: String = "dd.MM.yy",
    timeFmt: String = "HH:mm",
    fullFmt: String = "dd.MM.yy HH:mm",
    zone: ZoneId = ZoneId.systemDefault()
) {
    private var m_instant: Instant = Instant.now()
    private val m_dateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFmt).withZone(zone)
    private val m_timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern(timeFmt).withZone(zone)
    private val m_fullFmt: DateTimeFormatter = DateTimeFormatter.ofPattern(fullFmt).withZone(zone)

    public fun now() {
        m_instant = Instant.now()
    }

    public fun getDate() : String {
        return m_dateFmt.format(m_instant)
    }

    public fun getTime() : String {
        return m_timeFmt.format(m_instant)
    }

    public fun getFull() : String {
        return m_fullFmt.format(m_instant)
    }

    public fun cmp(timestamp: Timestamp) : Boolean {
        return (m_instant == timestamp.m_instant)
    }

    public fun export() : String {
        return m_instant.toString()
    }

    public fun import(data: String) {
        m_instant = Instant.parse(data)
    }
}