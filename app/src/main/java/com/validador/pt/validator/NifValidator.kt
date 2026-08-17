package com.validador.pt.validator

import com.validador.pt.data.model.ValidationResult

object NifValidator {
    private val WEIGHTS = intArrayOf(9, 8, 7, 6, 5, 4, 3, 2)
    
    private val ENTITY_TYPES = mapOf(
        '1' to "Pessoa singular",
        '2' to "Pessoa singular",
        '3' to "Pessoa singular",
        '5' to "Pessoa coletiva",
        '6' to "Pessoa coletiva pública",
        '8' to "Empresário em nome individual",
        '9' to "Pessoa coletiva irregular / provisório"
    )
    
    fun validate(nif: String): ValidationResult {
        val cleaned = nif.replace("\\s".toRegex(), "")
        
        if (cleaned.isEmpty()) {
            return ValidationResult.Empty
        }
        
        if (!cleaned.matches(Regex("^\\d{9}$"))) {
            return ValidationResult.Invalid("NIF deve ter 9 dígitos")
        }
        
        var sum = 0
        for (i in 0..7) {
            sum += cleaned[i].digitToInt() * WEIGHTS[i]
        }
        
        val remainder = sum % 11
        val control = if (remainder <= 1) 0 else 11 - remainder
        
        val isValid = cleaned[8].digitToInt() == control
        
        return if (isValid) {
            val entityType = ENTITY_TYPES[cleaned[0]] ?: "Tipo não reconhecido"
            ValidationResult.Valid(
                message = "NIF válido",
                entityType = entityType
            )
        } else {
            ValidationResult.Invalid("NIF inválido — dígito de controlo incorreto")
        }
    }
}
