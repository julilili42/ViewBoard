package com.example.viewboard.account

import org.mindrot.jbcrypt.BCrypt

class Account (
    private val name: String,       // immutable //
    private val eMail: String,
    private val password: String
) {
    private val m_name: String = name
    private var m_eMail: String = eMail
    private var m_pwHash: String = initPassword(password)

    public fun getName() : String {
        return m_name
    }

    public fun getEMail() : String {
        return m_eMail
    }

    public fun setEMail(eMail: String) {
        m_eMail = eMail
    }

    private fun initPassword(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    public fun setPassword(password: String) {
        m_pwHash = BCrypt.hashpw(password, BCrypt.gensalt())
    }

    public fun checkPassword(password: String) : Boolean {
        return BCrypt.checkpw(password, m_pwHash)
    }
}