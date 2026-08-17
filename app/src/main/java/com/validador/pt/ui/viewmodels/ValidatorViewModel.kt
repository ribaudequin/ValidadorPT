package com.validador.pt.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.validador.pt.data.BankRepository
import com.validador.pt.ui.state.ValidatorUiState
import com.validador.pt.data.model.ValidationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ValidatorViewModel(
    private val bankRepository: BankRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ValidatorUiState())
    val state: StateFlow<ValidatorUiState> = _state
    
    fun setInput(value: String, type: ValidationType) {
        _state.value = _state.value.copy(
            inputValue = value,
            currentType = type
        )
    }
    
    fun validate(type: ValidationType) {
        viewModelScope.launch {
            val input = _state.value.inputValue
            val result = when (type) {
                ValidationType.NIF -> validateNif(input)
                ValidationType.IBAN -> validateIban(input)
                ValidationType.NIB -> validateNib(input)
            }
            _state.value = _state.value.copy(
                validationResult = result,
                inputValue = input
            )
        }
    }
    
    private fun validateNif(input: String): String {
        return com.validador.pt.validator.NifValidator.validate(input).toString()
    }
    
    private fun validateIban(input: String): String {
        val cleanInput = input.replace("\\s".toRegex(), "").uppercase()
        val result = com.validador.pt.validator.IbanValidator.validate(cleanInput)
        // Resolve bank name
        return when (result) {
            is com.validador.pt.data.model.ValidationResult.Valid -> {
                val bankCode = com.validador.pt.validator.IbanValidator.extractBankCode(cleanInput)
                val bankName = bankRepository.getBankName(bankCode)
                if (bankName != null) {
                    "IBAN válido\nBanco: $bankName"
                } else {
                    "IBAN válido\nBanco: não identificado"
                }
            }
            is com.validador.pt.data.model.ValidationResult.Invalid -> result.message
            is com.validador.pt.data.model.ValidationResult.Empty -> ""
        }
    }
    
    private fun validateNib(input: String): String {
        val cleanInput = input.replace("\\s".toRegex(), "")
        val result = com.validador.pt.validator.NibValidator.validate(cleanInput)
        return when (result) {
            is com.validador.pt.data.model.ValidationResult.Valid -> {
                val bankCode = com.validador.pt.validator.NibValidator.extractBankCode(cleanInput)
                val bankName = bankRepository.getBankName(bankCode)
                if (bankName != null) {
                    "NIB válido\nBanco: $bankName"
                } else {
                    "NIB válido\nBanco: não identificado"
                }
            }
            is com.validador.pt.data.model.ValidationResult.Invalid -> result.message
            is com.validador.pt.data.model.ValidationResult.Empty -> ""
        }
    }
}

class ValidatorViewModelFactory(
    private val bankRepository: BankRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ValidatorViewModel(bankRepository) as T
    }
}
