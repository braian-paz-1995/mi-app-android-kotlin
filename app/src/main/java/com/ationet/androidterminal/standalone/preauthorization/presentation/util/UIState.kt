package com.ationet.androidterminal.standalone.preauthorization.presentation.util

sealed interface IdentificationTransaction
sealed interface SelectFuelTransaction
sealed interface FuelingOptionTransaction
sealed interface QuantityOrAmountTransaction
sealed interface ConfirmationTransaction
sealed interface SummaryTransaction

data object Loading
data class Success<T>(val data: T? = null) : IdentificationTransaction, SelectFuelTransaction, FuelingOptionTransaction, QuantityOrAmountTransaction,
    ConfirmationTransaction, SummaryTransaction

data class Error(val error: String)
data object Cancel : SelectFuelTransaction, FuelingOptionTransaction, ConfirmationTransaction, SummaryTransaction