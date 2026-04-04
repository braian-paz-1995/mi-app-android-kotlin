package com.ationet.androidterminal.core.data.local.room.entity.product

data class ProductQuantity(
    val inputType: InputType = InputType.Quantity,
    val value: Double = 0.0,
){
    enum class InputType {
        Quantity,
        Amount,
    }
}