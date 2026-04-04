package com.ationet.androidterminal.core.data.local.room.entity.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ationet.androidterminal.maintenance.product.domain.model.Product
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "products"
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String = "",
    @ColumnInfo(name = "unit_price") val unitPrice: Double = 0.0,
    @ColumnInfo(name = "created_at") val createAt: LocalDateTime,
    @ColumnInfo(name = "updated_at") val updateAt: LocalDateTime,
    @ColumnInfo(name = "is_fuel") val isFuel: Boolean = false,
    @ColumnInfo(name = "is_sku") val isSku: Boolean = false,
    @ColumnInfo(name = "order") val order: Int = 0
) {
    fun toDomain(): Product = Product(
        code = this.code,
        name = this.name,
        unitPrice = this.unitPrice,
        createAt = this.createAt,
        updateAt = this.updateAt,
        isFuel = this.isFuel,
        isSku = this.isSku,
        order = this.order
    )
}