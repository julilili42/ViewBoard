package com.example.viewboard

import com.example.viewboard.backend.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test

class TimestampUnitTest {
    @Test
    fun import_export() {
        val ts1 = Timestamp()

        val stream = ts1.export()

        val ts2 = Timestamp()

        assertEquals(ts1.cmp(ts2), false)

        ts2.import(stream)

        assertEquals(ts1.cmp(ts2), true)
    }
}