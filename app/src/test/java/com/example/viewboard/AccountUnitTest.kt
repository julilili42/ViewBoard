package com.example.viewboard

import com.example.viewboard.account.Account
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountUnitTest {
    @Test
    fun checkPassword() {
        val acc = Account("name", "eMail", "crazyPassword123")

        assertEquals(acc.checkPassword("crazyPassword123"), true)
        assertEquals(acc.checkPassword("123"), false)
    }
}