package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.coroutines.flow.Flow

interface ModulesCapabilityRepository {
    fun observeEnabledModules(): Flow<Set<Configuration.ModuleType>>
    suspend fun getEnabledModulesOnce(): Set<Configuration.ModuleType>
    suspend fun refreshIfNeeded(force: Boolean = false): Result<Set<Configuration.ModuleType>>
}
