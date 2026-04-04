package com.ationet.androidterminal.core.domain.util

import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierClass
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProduct

class CompanyPrice(
    val transaction: ReceiptProduct,
    val quantity: Double?
) {
    fun calculateSurcharge(): Double {
        var totalPercentage = 0.0
        var totalFixedUnit = 0.0
        var totalFixedTransaction = 0.0
        transaction.modifiers.filter { it.modifierClass == ReceiptModifierClass.Surcharge }.forEach { modifier ->
            when (modifier.type) {
                ReceiptModifierType.Percentage -> totalPercentage += modifier.total
                ReceiptModifierType.FixedUnit -> totalFixedUnit += modifier.total
                ReceiptModifierType.FixedTransaction -> totalFixedTransaction += modifier.total
            }
        }
        return ((totalPercentage + totalFixedUnit) * (quantity ?: 0.0)) + totalFixedTransaction
    }

    fun calculateDiscountsAndRebates(): Double {
        var totalPercentage = 0.0
        var totalFixedUnit = 0.0
        var totalFixedTransaction = 0.0
        transaction.modifiers.filter { it.modifierClass == ReceiptModifierClass.Discount }.forEach { modifier ->
            when (modifier.type) {
                ReceiptModifierType.Percentage -> totalPercentage += modifier.total
                ReceiptModifierType.FixedUnit -> totalFixedUnit += modifier.total
                ReceiptModifierType.FixedTransaction -> totalFixedTransaction += modifier.total
            }
        }
        return ((totalPercentage + totalFixedUnit) * (quantity ?: 0.0)) + totalFixedTransaction
    }
}