package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.task.rechargeCC.domain.model.RechargeCC

interface RechargeCCRepository {
    suspend fun createRechargeCC(rechargeCC: RechargeCC): RechargeCC
    suspend fun getRechargeCC(id: Int): RechargeCC?
    suspend fun getAllRechargeCC(): List<RechargeCC>
    suspend fun deleteRechargeCC(id: Int) : Boolean
}