package com.ationet.androidterminal.core.domain.model.configuration

data class Fusion(
    val fusionIp: String = Configuration.Companion.Defaults.DEFAULT_FUSION_IP,
    val fusionPort: String = Configuration.Companion.Defaults.DEFAULT_FUSION_PORT,
    val fusionPaymentTypeCode: String = Configuration.Companion.Defaults.DEFAULT_FUSION_PAYMENT_TYPE_CODE,
    val fusionAuthorizationCode: String = Configuration.Companion.Defaults.DEFAULT_FUSION_AUTHORIZATION_CODE_TAG,
    val fusionPaymentMethod: Boolean = Configuration.Companion.Defaults.DEFAULT_FUSION_PAYMENT_METHOD,
    val enableFinalizationVariance: Boolean = Configuration.Companion.Defaults.DEFAULT_ENABLE_FINALIZATION_VARIANCE

)