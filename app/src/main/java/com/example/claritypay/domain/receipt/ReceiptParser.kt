package com.example.claritypay.domain.receipt

import com.example.claritypay.domain.models.ScannedReceipt
import kotlin.math.max

object ReceiptParser {
    private val totalLineRegex = Regex(
        pattern = """(?i)\b(total|importe|monto|pago|venta|cobro)\b[^\d$-]{0,18}\$?\s*(-?\d{1,3}(?:[,.]\d{3})*(?:[,.]\d{2})|-?\d+(?:[,.]\d{2})|-?\d+)"""
    )
    private val amountRegex = Regex("""\$?\s*(-?\d{1,3}(?:[,.]\d{3})*(?:[,.]\d{2})|-?\d+(?:[,.]\d{2}))""")
    private val dateRegex = Regex("""\b(\d{1,2}[/-]\d{1,2}[/-]\d{2,4}|\d{4}[/-]\d{1,2}[/-]\d{1,2})\b""")
    private val receiptKeywords = listOf(
        "ticket",
        "recibo",
        "factura",
        "folio",
        "rfc",
        "subtotal",
        "total",
        "iva",
        "cambio",
        "efectivo",
        "tarjeta"
    )

    fun looksLikeReceipt(rawText: String): Boolean {
        val text = rawText.trim()
        if (text.length < 24) return false

        val lowercase = text.lowercase()
        val keywordScore = receiptKeywords.count { lowercase.contains(it) }
        val hasTotal = totalLineRegex.containsMatchIn(text)
        val hasMoney = amountRegex.findAll(text).count() >= 2

        return (hasTotal && keywordScore >= 1) || (keywordScore >= 3 && hasMoney)
    }

    fun parse(rawText: String): ScannedReceipt {
        val cleanLines = rawText
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toList()

        val merchant = cleanLines.firstOrNull { line ->
            val normalized = line.lowercase()
            line.length >= 3 &&
                !normalized.contains("total") &&
                !normalized.contains("rfc") &&
                !normalized.contains("folio") &&
                !normalized.contains("fecha") &&
                amountRegex.find(line) == null
        }.orEmpty()

        val detectedAmount = findTotalAmount(rawText)
        val ticketType = inferTicketType(rawText)

        return ScannedReceipt(
            title = merchant.ifBlank { "Ticket escaneado" }.take(40),
            amount = detectedAmount,
            category = categoryFor(ticketType),
            ticketType = ticketType,
            dateLabel = dateRegex.find(rawText)?.value ?: "Hoy",
            rawText = rawText.trim()
        )
    }

    private fun findTotalAmount(rawText: String): Double {
        val totals = totalLineRegex.findAll(rawText)
            .mapNotNull { match -> normalizeAmount(match.groupValues.getOrNull(2).orEmpty()) }
            .toList()

        if (totals.isNotEmpty()) {
            return totals.maxOrNull() ?: 0.0
        }

        return amountRegex.findAll(rawText)
            .mapNotNull { match -> normalizeAmount(match.groupValues.getOrNull(1).orEmpty()) }
            .filter { it > 0.0 }
            .fold(0.0) { biggest, amount -> max(biggest, amount) }
    }

    private fun normalizeAmount(rawAmount: String): Double? {
        val value = rawAmount.filter { it.isDigit() || it == ',' || it == '.' || it == '-' }
        if (value.isBlank()) return null

        val decimalSeparator = when {
            value.lastIndexOf('.') > value.lastIndexOf(',') -> '.'
            value.lastIndexOf(',') > value.lastIndexOf('.') -> ','
            else -> '.'
        }

        val normalized = buildString {
            value.forEachIndexed { index, char ->
                when {
                    char.isDigit() || char == '-' -> append(char)
                    char == decimalSeparator && index == value.lastIndexOf(decimalSeparator) -> append('.')
                }
            }
        }

        return normalized.toDoubleOrNull()
    }

    private fun inferTicketType(rawText: String): String {
        val text = rawText.lowercase()
        return when {
            listOf("restaurante", "restaurant", "cafe", "taqueria", "pizza", "burger", "bar").any { text.contains(it) } -> "Comida"
            listOf("oxxo", "walmart", "soriana", "chedraui", "super", "mercado", "costco", "seven").any { text.contains(it) } -> "Supermercado"
            listOf("gasolina", "pemex", "shell", "uber", "didi", "taxi", "estacionamiento").any { text.contains(it) } -> "Movilidad"
            listOf("cfe", "telmex", "telcel", "internet", "agua", "luz", "servicio").any { text.contains(it) } -> "Servicio"
            listOf("farmacia", "doctor", "medic", "salud").any { text.contains(it) } -> "Salud"
            else -> "Compra"
        }
    }

    private fun categoryFor(ticketType: String): String =
        when (ticketType) {
            "Comida" -> "Entretenimiento"
            "Supermercado" -> "Casa"
            "Movilidad" -> "Movilidad"
            "Servicio" -> "Servicios"
            "Salud" -> "Personal"
            else -> "Otro"
        }
}
