package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.standalone.completion.domain.model.Completion

interface CompletionRepository {
    suspend fun createCompletion(completion: Completion): Completion
    suspend fun getCompletion(id: Int): Completion?
    suspend fun getAllCompletion(): List<Completion>
    suspend fun deleteCompletion(id: Int) : Boolean
}