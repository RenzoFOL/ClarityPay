package com.example.claritypay.domain.receipt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReceiptParserTest {
    @Test
    fun parsesTotalMerchantAndCategoryFromReceiptText() {
        val rawText = """
            OXXO Universidad
            RFC OXX010101ABC
            Folio 19384
            Subtotal $84.48
            IVA $13.52
            TOTAL $98.00
            Efectivo
        """.trimIndent()

        val receipt = ReceiptParser.parse(rawText)

        assertTrue(ReceiptParser.looksLikeReceipt(rawText))
        assertEquals("OXXO Universidad", receipt.title)
        assertEquals(98.0, receipt.amount, 0.0)
        assertEquals("Supermercado", receipt.ticketType)
        assertEquals("Casa", receipt.category)
    }
}
