package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_loyaltyBalanceEnquiry",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["loyaltyBalanceEnquiry_id"],
            entity = LoyaltyBalanceEnquiryEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("loyaltyBalanceEnquiry_id")]
)
data class FusionLoyaltyBalanceEnquiryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_loyaltyBalanceEnquiry_id")
    val fusionLoyaltyBalanceEnquiryId: Int,
    @ColumnInfo(name = "loyaltyBalanceEnquiry_id")
    val loyaltyBalanceEnquiryId: Int,
)

