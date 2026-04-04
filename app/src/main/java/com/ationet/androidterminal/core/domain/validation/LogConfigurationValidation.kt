package com.atio.nat.terminal_management.domain.validation

object LogConfigurationValidation {
    fun isValidFileSize(size: Long): Boolean {
        if (size < 1) {
            return false
        }

        if(size > 100) {
            return false
        }

        return true
    }

    fun isValidFileQuantity(quantity: Int): Boolean {
        if (quantity < 1) {
            return false
        }

        if(quantity > 10) {
            return false
        }

        return true
    }
}