package com.example.viewboard.label

/**
 * @property name the name of the label
 */
class Label (
    private val name: String
) {
    private var m_name: String = name

    /**
     * Get the name
     *
     * @return the name
     */
    public fun getName() : String {
        return m_name
    }

    /**
     * Set the name
     *
     * @return the name
     */
    public fun setName() {
        m_name = name
    }
}