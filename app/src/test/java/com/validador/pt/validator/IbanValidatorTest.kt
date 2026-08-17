package com.validador.pt.validator

import com.validador.pt.data.model.ValidationResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IbanValidatorTest {

    @Nested
    inner class ValidIbanTests {
        @Test
        fun `valid PT IBAN returns Valid and extracts bank code`() {
            val iban = "PT03000100010123456789012" // Valid PT IBAN generated for testing
            val result = IbanValidator.validate(iban)
            assertTrue(result is ValidationResult.Valid)
            val valid = result as ValidationResult.Valid
            assertEquals("IBAN válido", valid.message)
            // Bank code extraction should return "0001"
            assertEquals("0001", IbanValidator.extractBankCode(iban))
        }
    }

    @Nested
    inner class InvalidIbanTests {
        @Test
        fun `invalid IBAN with wrong check digits returns Invalid`() {
            val iban = "PT03000100010123456789013" // Last digit altered
            val result = IbanValidator.validate(iban)
            assertTrue(result is ValidationResult.Invalid)
            val invalid = result as ValidationResult.Invalid
            assertEquals("IBAN inválido — falha no módulo 97", invalid.message)
        }

        @Test
        fun `non‑PT IBAN returns Invalid`() {
            val iban = "ES6600190021234567891234"
            val result = IbanValidator.validate(iban)
            assertTrue(result is ValidationResult.Invalid)
        }

        @Test
        fun `IBAN with wrong format returns Invalid`() {
            val iban = "PT12345"
            val result = IbanValidator.validate(iban)
            assertTrue(result is ValidationResult.Invalid)
        }
    }

    @Test
    fun `empty input returns Empty`() {
        val result = IbanValidator.validate("")
        assertTrue(result is ValidationResult.Empty)
    }
}
