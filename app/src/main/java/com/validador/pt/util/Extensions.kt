package com.validador.pt.util

fun String.cleanSpaces(): String {
    return this.replace("\\s".toRegex(), "")
}

fun String.isValidDigitsOnly(length: Int): Boolean {
    val cleaned = this.cleanSpaces()
    return cleaned.length == length && cleaned.all { it.isDigit() }
}
