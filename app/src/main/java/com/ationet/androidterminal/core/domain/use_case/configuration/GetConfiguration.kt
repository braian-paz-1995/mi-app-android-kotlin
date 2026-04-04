package com.ationet.androidterminal.core.domain.use_case.configuration

import com.ationet.androidterminal.core.domain.repository.ConfigurationRepository
import javax.inject.Inject

class GetConfiguration @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    operator fun invoke() = configurationRepository.get()
}