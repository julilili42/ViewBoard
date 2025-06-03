package com.example.viewboard.account

import org.mindrot.jbcrypt.BCrypt

/**
 * @property name the name which is also immutable
 * @property eMail the E-Mail
 * @property password the password in plain text
 */
class Account (
    private val name: String,
    private val eMail: String,
    private val password: String
) {
    private val m_name: String = name
    private var m_eMail: String = eMail
    private var m_pwHash: String = initPassword(password)

    /**
     * Get the name
     *
     * @return the name
     */
    public fun getName() : String {
        return m_name
    }

    /**
     * Get the E-Mail
     *
     * @return the E-Mail
     */
    public fun getEMail() : String {
        return m_eMail
    }

    /**
     * Set the E-Mail
     *
     * @param eMail the E-Mail
     */
    public fun setEMail(eMail: String) {
        m_eMail = eMail
    }

    /**
     * Set the password
     *
     * @param password the password in plain text
     */
    public fun setPassword(password: String) {
        m_pwHash = BCrypt.hashpw(password, BCrypt.gensalt())
    }

    /**
     * Checks if the password matches
     *
     * @param password the password in plain text
     * @return true if the password matches, otherwise false
     */
    public fun checkPassword(password: String) : Boolean {
        return BCrypt.checkpw(password, m_pwHash)
    }

    // only for internal initialization usage //
    private fun initPassword(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}