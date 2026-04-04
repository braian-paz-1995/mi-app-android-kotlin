package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.configuration.Configuration

abstract class ConfigurationRepository {
    protected abstract val currentConfiguration: Configuration?
    abstract fun get(): Configuration
    abstract fun update(block: (Configuration) -> Configuration): Configuration
}