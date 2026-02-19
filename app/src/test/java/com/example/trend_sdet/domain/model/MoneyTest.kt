package com.example.trend_sdet.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MoneyTest {

    @Test
    fun `USD formats with dollar sign prefix`() {
        assertThat(Money("29.99", "USD").formatted).isEqualTo("$29.99")
    }

    @Test
    fun `EUR formats with euro sign suffix`() {
        assertThat(Money("15.00", "EUR").formatted).isEqualTo("15.00\u20AC")
    }

    @Test
    fun `TRY formats with lira sign suffix`() {
        assertThat(Money("100.50", "TRY").formatted).isEqualTo("100.50\u20BA")
    }

    @Test
    fun `GBP formats with pound sign prefix`() {
        assertThat(Money("42.00", "GBP").formatted).isEqualTo("\u00A342.00")
    }

    @Test
    fun `unknown currency uses code suffix`() {
        assertThat(Money("10.00", "JPY").formatted).isEqualTo("10.00 JPY")
    }

    @Test
    fun `invalid amount defaults to zero`() {
        assertThat(Money("not-a-number", "USD").formatted).isEqualTo("$0.00")
    }
}
