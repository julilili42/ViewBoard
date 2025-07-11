package com.example.viewboard.backend

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @property dateFmt the default date format
 * @property timeFmt the default time format
 * @property fullFmt the default date-time format
 * @property zone the default zone
 * @property data data is used to import a timestamp exported string
 */
class Timestamp (
    dateFmt: String = "dd.MM.yy",
    timeFmt: String = "HH:mm",
    fullFmt: String = "dd.MM.yy HH:mm",
    zone: ZoneId = ZoneId.systemDefault(),
    data: String? = null
) {
    private var m_instant: Instant = data?.let(Instant::parse) ?: Instant.now()
    private val m_dateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFmt).withZone(zone)
    private val m_timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern(timeFmt).withZone(zone)
    private val m_fullFmt: DateTimeFormatter = DateTimeFormatter.ofPattern(fullFmt).withZone(zone)

    /**
     * Set current time
     */
    public fun now() {
        m_instant = Instant.now()
    }

    /**
     * Get the date
     *
     * @return the date in default format
     */
    public fun getDate() : String {
        return m_dateFmt.format(m_instant)
    }

    /**
     * Get the time
     *
     * @return the time in default format
     */
    public fun getTime() : String {
        return m_timeFmt.format(m_instant)
    }

    /**
     * Get the date-time
     *
     * @return the date-time in default format
     */
    public fun getFull() : String {
        return m_fullFmt.format(m_instant)
    }

    /**
     * Get date/time in custom format and zone
     *
     * @param customFmt the custom format
     * @param zone the custom zone
     *
     * @return the date/time in custom format and zone
     */
    public fun getCustom(customFmt: String, zone: ZoneId) : String {
        val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern(customFmt).withZone(zone)
        return fmt.format(m_instant)
    }

    /**
     * Compares 2 timestamps
     *
     * @param timestamp the other timestamp
     *
     * @return true is the timestamps are equal
     */
    public fun cmp(timestamp: Timestamp) : Boolean {
        return (m_instant == timestamp.m_instant)
    }

    /**
     * Export the timestamp
     *
     * @return the timestamp as string
     */
    public fun export() : String {
        return m_instant.toString()
    }

    /**
     * Import timestamp as string
     *
     * @param data the timestamp as string
     */
    public fun import(data: String) {
        m_instant = Instant.parse(data)
    }

    /**
     * Import timestamp as instant
     *
     * @param instant the timestamp as instant
     */
    public fun import(instant: Instant) {
        m_instant = instant
    }
}