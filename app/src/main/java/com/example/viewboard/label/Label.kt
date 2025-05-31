package com.example.viewboard.label

class Label (
    private val name: String
) {
    private var m_name: String = name

    public fun getName() : String {
        return m_name
    }

    public fun setName() {
        m_name = name
    }
}