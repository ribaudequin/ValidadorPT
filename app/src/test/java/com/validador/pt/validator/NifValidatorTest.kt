package com.validador.pt.validator

import com.validador.pt.data.model.ValidationResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NifValidatorTest {

    @Nested
    inner class ValidNifTests {

        @Nested
        inner class PessoaSingularTests {
            @Test
            fun `NIF starting with 1 is pessoa singular`() {
                // 123456789: sum=1*9+2*8+3*7+4*6+5*5+6*4+7*3+8*2=156, 156%11=2, 11-2=9 ✓
                val result = NifValidator.validate("123456789")
                assertTrue(result is ValidationResult.Valid)
                assertEquals("Pessoa singular", (result as ValidationResult.Valid).entityType)
            }

            @Test
            fun `NIF starting with 2 is pessoa singular`() {
                // 200000004: sum=2*9=18, 18%11=7, 11-7=4 ✓
                val result = NifValidator.validate("200000004")
                assertTrue(result is ValidationResult.Valid)
                assertEquals("Pessoa singular", (result as ValidationResult.Valid).entityType)
            }

            @Test
            fun `NIF starting with 3 is pessoa singular`() {
                // 300000006: sum=3*9=27, 27%11=5, 11-5=6 ✓
                val result = NifValidator.validate("300000006")
                assertTrue(result is ValidationResult.Valid)
                assertEquals("Pessoa singular", (result as ValidationResult.Valid).entityType)
            }
        }

        @Test
        fun `NIF starting with 5 is pessoa coletiva`() {
            // 501442600: sum=122, 122%11=1, remainder<=1 so control=0 ✓
            val result = NifValidator.validate("501442600")
            assertTrue(result is ValidationResult.Valid)
            val valid = result as ValidationResult.Valid
            assertEquals("NIF válido", valid.message)
            assertEquals("Pessoa coletiva", valid.entityType)
        }

        @Test
        fun `NIF starting with 6 is pessoa coletiva publica`() {
            // 600123456: sum=6*9+0*8+0*7+1*6+2*5+3*4+4*3+5*2=104, 104%11=5, 11-5=6 ✓
            val result = NifValidator.validate("600123456")
            assertTrue(result is ValidationResult.Valid)
            assertEquals("Pessoa coletiva pública", (result as ValidationResult.Valid).entityType)
        }

        @Test
        fun `NIF starting with 8 is empresario em nome individual`() {
            // 801234565: sum=8*9+0*8+1*7+2*6+3*5+4*4+5*3+6*2=149, 149%11=6, 11-6=5 ✓
            val result = NifValidator.validate("801234565")
            assertTrue(result is ValidationResult.Valid)
            assertEquals("Empresário em nome individual", (result as ValidationResult.Valid).entityType)
        }

        @Test
        fun `NIF starting with 9 is pessoa coletiva irregular`() {
            // 900123451: sum=9*9+0*8+0*7+1*6+2*5+3*4+4*3+5*2=131, 131%11=10, 11-10=1 ✓
            val result = NifValidator.validate("900123451")
            assertTrue(result is ValidationResult.Valid)
            assertEquals("Pessoa coletiva irregular / provisório", (result as ValidationResult.Valid).entityType)
        }

        @Test
        fun `NIF with leading and trailing whitespace is accepted`() {
            val result = NifValidator.validate("  501442600  ")
            assertTrue(result is ValidationResult.Valid)
            assertEquals("Pessoa coletiva", (result as ValidationResult.Valid).entityType)
        }
    }

    @Nested
    inner class InvalidNifTests {
        @Test
        fun `invalid NIF with wrong check digit returns Invalid`() {
            // 501442600 is valid; altering last digit to 1 makes it invalid
            val result = NifValidator.validate("501442601")
            assertTrue(result is ValidationResult.Invalid)
            val invalid = result as ValidationResult.Invalid
            assertEquals("NIF inválido — dígito de controlo incorreto", invalid.message)
        }

        @Test
        fun `non-numeric NIF returns Invalid`() {
            val result = NifValidator.validate("ABCDEF123")
            assertTrue(result is ValidationResult.Invalid)
            assertEquals("NIF deve ter 9 dígitos", (result as ValidationResult.Invalid).message)
        }

        @Test
        fun `NIF with wrong length (too short) returns Invalid`() {
            val result = NifValidator.validate("12345678")
            assertTrue(result is ValidationResult.Invalid)
            assertEquals("NIF deve ter 9 dígitos", (result as ValidationResult.Invalid).message)
        }

        @Test
        fun `NIF with wrong length (too long) returns Invalid`() {
            val result = NifValidator.validate("1234567890")
            assertTrue(result is ValidationResult.Invalid)
        }

        @Test
        fun `NIF with letters embedded returns Invalid`() {
            val result = NifValidator.validate("1234A6789")
            assertTrue(result is ValidationResult.Invalid)
        }

        @Test
        fun `NIF with special characters returns Invalid`() {
            val result = NifValidator.validate("1234-6789")
            assertTrue(result is ValidationResult.Invalid)
        }
    }

    @Nested
    inner class EmptyInputTests {
        @Test
        fun `empty string returns Empty`() {
            val result = NifValidator.validate("")
            assertTrue(result is ValidationResult.Empty)
        }

        @Test
        fun `whitespace-only returns Empty`() {
            val result = NifValidator.validate("   ")
            assertTrue(result is ValidationResult.Empty)
        }
    }

    @Nested
    inner class EdgeCaseTests {
        @Test
        fun `NIF starting with 0 validates mathematically but entity type is unknown`() {
            // 000000000: sum=0, remainder=0<=1, control=0 ✓
            val result = NifValidator.validate("000000000")
            assertTrue(result is ValidationResult.Valid)
            val valid = result as ValidationResult.Valid
            assertEquals("Tipo não reconhecido", valid.entityType)
        }

        @Test
        fun `NIF starting with 4 validates mathematically but entity type is unknown`() {
            // 400000008: sum=4*9=36, 36%11=3, 11-3=8 ✓
            val result = NifValidator.validate("400000008")
            assertTrue(result is ValidationResult.Valid)
            val valid = result as ValidationResult.Valid
            assertEquals("Tipo não reconhecido", valid.entityType)
        }

        @Test
        fun `NIF with remainder less or equal 1 uses control digit 0`() {
            // 500000000: sum=5*9=45, 45%11=1, remainder<=1 so control=0 ✓
            val result = NifValidator.validate("500000000")
            assertTrue(result is ValidationResult.Valid)
        }

        @Test
        fun `NIF where remainder is exactly 0 uses control digit 0`() {
            // 000000000: sum=0, 0%11=0, remainder<=1 so control=0 ✓
            val result = NifValidator.validate("000000000")
            assertTrue(result is ValidationResult.Valid)
        }
    }
}
