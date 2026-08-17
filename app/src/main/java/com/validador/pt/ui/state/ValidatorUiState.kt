package com.validador.pt.ui.state

import com.validador.pt.data.model.ValidationType

data class ValidatorUiState(
    val inputValue: String = "",
    val currentType: ValidationType = ValidationType.NIF,
    val validationResult: String? = null
)
