package com.ationet.androidterminal.core.domain.model.configuration

import com.ationet.androidterminal.core.domain.model.configuration.Configuration.Companion.Defaults

data class Site(
    val siteCode: String = Defaults.DEFAULT_SITE_CODE,
    val siteName: String = Defaults.DEFAULT_SITE_NAME,
    val siteAddress: String = Defaults.DEFAULT_SITE_ADDRESS,
    val siteCuit: String = Defaults.DEFAULT_SITE_CUIT,
)