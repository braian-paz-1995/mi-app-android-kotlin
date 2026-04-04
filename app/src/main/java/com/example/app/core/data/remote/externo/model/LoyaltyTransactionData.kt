package com.ationet.androidterminal.core.data.remote.ationet.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoyaltyTransactionData(
    @SerialName("Products")
    val products: List<LoyaltyProduct?>,
    @SerialName("ReceiptData")
    val receiptData: LoyaltyReceiptData? = null,
    @SerialName("PaymentsMethods")
    val paymentsMethods: List<NativePaymentMethod?>,
)


@Serializable
data class NativePaymentMethod(
    @SerialName("PaymentsMethodCode")
    val paymentsMethodCode: String? = null,
    @SerialName("Amount")
    val amount: String? = null
)

@OptIn(ExperimentalSerializationApi::class)
private val jsonLoose = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

fun parseLoyaltyReceiptData(raw: String?): LoyaltyReceiptData? =
    raw?.let {
        runCatching { jsonLoose.decodeFromString(LoyaltyReceiptData.serializer(), it) }
            .getOrNull()
    }



@Serializable
data class LoyaltyProduct(

    @SerialName("UnitCode")
    val unitCode: String? = null,

    @SerialName("ProductCode")
    val productCode: String? = null,

    @SerialName("ProductUnitPrice")
    val productUnitPrice: Double? = null,

    @SerialName("ProductAmount")
    val productAmount: Double? = null,

    @SerialName("ProductQuantity")
    val productQuantity: Double? = null,

    @SerialName("ProductTotalAmount")
    val productTotalAmount: Double? = null,

    @SerialName("ProductTotalQuantity")
    val productTotalQuantity: Double? = null,

    @SerialName("ProductDiscount")
    val productDiscount: Double? = null,

    @SerialName("DiscountValue")
    val discountValue: Double? = null,

    @SerialName("DiscountRuleCode")
    val discountRuleCode: String? = null,

    @SerialName("DiscountRuleDescription")
    val discountRuleDescription: String? = null
)

@Serializable
data class LoyaltyReceiptData(
    @SerialName("CustomerName") val customerName: String? = null,
    @SerialName("CustomerTaxPayerId") val customerTaxPayerId: String? = null,
    @SerialName("CustomerPlate") val customerPlate: String? = null,
    @SerialName("CustomerIdentification") val customerIdentification: String? = null,
    @SerialName("CustomerPAN") val customerPAN: String? = null,
    @SerialName("CompanyName") val companyName: String? = null,
    @SerialName("LoyaltyProgram") val loyaltyProgram: String? = null,
    @SerialName("LoyaltyProgramUnit") val loyaltyProgramUnit: String? = null,
    @SerialName("LoyaltyProgramPoints") val loyaltyProgramPoints: String? = null,
    @SerialName("LoyaltyProgramBalance") val loyaltyProgramBalance: String? = null,
    @SerialName("LoyaltyNextExpirationDate") val loyaltyNextExpirationDate: String? = null,
    @SerialName("LoyaltyNextExpirationPoints") val loyaltyNextExpirationPoints: String? = null,
    @SerialName("LoyaltyProgramDiscount") val loyaltyProgramDiscount: String? = null,
    @SerialName("LoyaltyProgramSupportPhone") val loyaltyProgramSupportPhone: String? = null,
    @SerialName("LoyaltyProgramDiscountLimit") val loyaltyProgramDiscountLimit: String? = null,
    @SerialName("LoyaltyProgramDiscountValue") val loyaltyProgramDiscountValue: String? = null,
    @SerialName("LoyaltyProgramEliteStatus") val loyaltyProgramEliteStatus: String? = null,
    @SerialName("WithdrawalCode") val withdrawalCode: String? = null,
    @SerialName("SiteName") val siteName: String? = null,
    @SerialName("SiteAddress") val siteAddress: String? = null,
    @SerialName("LocalTransactionDateTime") val localTransactionDateTime: String? = null,
    @SerialName("CustomerLoyaltyCardPAN") val customerLoyaltyCardPAN: String? = null,
    @SerialName("RedeemedPrizes")
    val redeemedPrizes: List<Prize?>? = emptyList(),
    @SerialName("SKURewardAmount") val skuRewardAmount: String? = null,
    @SerialName("SKURewardQuantity") val skuRewardQuantity: String? = null
)

@Serializable
data class Prize(
    val id: Int?,
    val description: String?
)