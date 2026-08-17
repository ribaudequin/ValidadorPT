package com.validador.pt.validator

import com.validador.pt.data.model.ValidationResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NibValidatorTest {

    @Nested
    inner class ValidNibTests {
        @Test
        fun `valid NIB returns Valid`() {
            val nib = "003503840000017300718"
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Valid)
            val valid = result as ValidationResult.Valid
            assertEquals("NIB válido", valid.message)
        }

        @Test
        fun `valid NIB with different bank returns Valid`() {
            val nib = "000100000001234567851"
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Valid)
        }

        @Test
        fun `valid NIB with leading spaces is accepted`() {
            val nib = "  003503840000017300718  "
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Valid)
        }
    }

    @Nested
    inner class InvalidNibTests {
        @Test
        fun `invalid NIB with wrong check digits returns Invalid`() {
            val nib = "003503840000017300719" // last digit altered
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Invalid)
            val invalid = result as ValidationResult.Invalid
            assertEquals("NIB inválido — dígito de controlo incorreto", invalid.message)
        }

        @Test
        fun `NIB with wrong length returns Invalid`() {
            val nib = "00350384000001730071" // 20 digits
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Invalid)
            assertEquals("NIB deve ter 21 dígitos", (result as ValidationResult.Invalid).message)
        }

        @Test
        fun `NIB with 22 digits returns Invalid`() {
            val nib = "0035038400000173007180"
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Invalid)
        }

        @Test
        fun `non‑numeric NIB returns Invalid`() {
            val nib = "0035ABCDEFG123456789"
            val result = NibValidator.validate(nib)
            assertTrue(result is ValidationResult.Invalid)
        }
    }

    @Nested
    inner class BankCodeExtractionTests {
        @Test
        fun `extractBankCode returns first 4 digits`() {
            assertEquals("0035", NibValidator.extractBankCode("003503840000017300718"))
        }

        @Test
        fun `extractBankCode with spaces trims and extracts`() {
            assertEquals("0001", NibValidator.extractBankCode("  000100000001234567851  "))
        }

        @Test
        fun `extractBankCode with short input returns empty`() {
            assertEquals("", NibValidator.extractBankCode("123"))
        }
    }

    @Test
    fun `empty input returns Empty`() {
        val result = NibValidator.validate("")
        assertTrue(result is ValidationResult.Empty)
    }
}
