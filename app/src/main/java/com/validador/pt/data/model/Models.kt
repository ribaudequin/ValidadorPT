package com.validador.pt.data.model

data class Bank(
    val code: String,
    val name: String,
    val swift: String? = null
)

sealed class ValidationResult {
    data class Valid(
        val message: String,
        val bankName: String? = null,
        val entityType: String? = null
    ) : ValidationResult()
    
    data class Invalid(
        val message: String
    ) : ValidationResult()
    
    object Empty : ValidationResult()
}
