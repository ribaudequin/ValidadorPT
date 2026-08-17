package com.validador.pt.validator

import com.validador.pt.data.model.ValidationResult

object IbanValidator {
    fun validate(iban: String): ValidationResult {
        val cleaned = iban.replace("\\s".toRegex(), "").uppercase()
        
        if (cleaned.isEmpty()) {
            return ValidationResult.Empty
        }
        
        if (!cleaned.matches(Regex("^PT\\d{23}$"))) {
            return ValidationResult.Invalid("IBAN deve começar com PT seguido de 23 dígitos")
        }
        
        val rearranged = cleaned.substring(4) + cleaned.substring(0, 4)
        val numeric = buildString {
            for (ch in rearranged) {
                if (ch.isLetter()) {
                    append(ch.code - 'A'.code + 10)
                } else {
                    append(ch)
                }
            }
        }
        
        var remainder = 0
        for (digit in numeric) {
            remainder = (remainder * 10 + digit.digitToInt()) % 97
        }
        
        val isValid = remainder == 1
        
        return if (isValid) {
            val bankCode = cleaned.substring(4, 8)
            ValidationResult.Valid(
                message = "IBAN válido",
                bankName = null // BankRepository will resolve this
            )
        } else {
            ValidationResult.Invalid("IBAN inválido — falha no módulo 97")
        }
    }
    
    fun extractBankCode(iban: String): String {
        val cleaned = iban.replace("\\s".toRegex(), "").uppercase()
        return if (cleaned.length >= 8) cleaned.substring(4, 8) else ""
    }
}
