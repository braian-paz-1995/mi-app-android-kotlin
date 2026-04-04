package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.task.reverseCC.domain.model.ReverseCC

interface ReverseCCRepository {
    suspend fun createReverseCC(reverseCC: ReverseCC): ReverseCC
    suspend fun getReverseCC(id: Int): ReverseCC?
    suspend fun getAllReverseCC(): List<ReverseCC>
    suspend fun deleteReverseCC(id: Int) : Boolean
}