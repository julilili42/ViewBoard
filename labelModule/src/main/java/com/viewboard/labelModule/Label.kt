package com.viewBoard.labelModule

import com.viewBoard.commonModule.Timestamp

/**
 * @property name the name of the label
 * @property creator the creator of the label
 * @property refCounter the ref counter of the label, default = 0
 * @property timestamp the timestamp of the label, default = current timestamp
 */
class Label
internal constructor (
    name: String,
    creator: String,
    refCounter: UInt = 0u,
    timestamp: Timestamp = Timestamp()
) {
    private val m_name: String = name
    private val m_creator: String = creator
    private var m_refCounter: UInt = refCounter
    private val m_timestamp: Timestamp = timestamp

    /**
     * Get the name
     *
     * @return the name
     */
    public fun getName() : String {
        return m_name
    }

    /**
     * Get the creator
     *
     * @return the creator
     */
    public fun getCreator() : String {
        return m_creator
    }

    /**
     * Get the ref counter
     *
     * @return the ref counter
     */
    public fun getRefCounter() : UInt {
        return m_refCounter
    }

    /**
     * Get the timestamp
     *
     * @return the timestamp
     */
    public fun getTimestamp() : Timestamp {
        return m_timestamp
    }

    public fun cmp(label: Label) : Boolean {
        return (m_name == label.m_name)
    }

    internal fun addUsage() {
        m_refCounter++
    }

    internal fun removeUsage() {
        m_refCounter--
    }
}