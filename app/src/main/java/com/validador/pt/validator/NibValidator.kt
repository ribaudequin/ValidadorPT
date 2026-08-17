package com.validador.pt.validator

import com.validador.pt.data.model.ValidationResult

object NibValidator {
    private val PESOS = intArrayOf(
        73, 17, 89, 38, 62, 45, 53, 15, 50, 5,
        49, 34, 81, 76, 27, 90, 9, 30, 3
    )
    
    fun validate(nib: String): ValidationResult {
        val cleaned = nib.replace("\\s".toRegex(), "")
        
        if (cleaned.isEmpty()) {
            return ValidationResult.Empty
        }
        
        if (!cleaned.matches(Regex("^\\d{21}$"))) {
            return ValidationResult.Invalid("NIB deve ter 21 dígitos")
        }
        
        var sum = 0
        for (i in 0..18) {
            sum += cleaned[i].digitToInt() * PESOS[i]
        }
        
        val control = 98 - (sum % 97)
        val expectedControl = cleaned.substring(19, 21).toInt()
        
        val isValid = control == expectedControl
        
        return if (isValid) {
            ValidationResult.Valid(
                message = "NIB válido",
                bankName = null
            )
        } else {
            ValidationResult.Invalid("NIB inválido — dígito de controlo incorreto")
        }
    }
    
    fun extractBankCode(nib: String): String {
        val cleaned = nib.replace("\\s".toRegex(), "")
        return if (cleaned.length >= 4) cleaned.substring(0, 4) else ""
    }
}
