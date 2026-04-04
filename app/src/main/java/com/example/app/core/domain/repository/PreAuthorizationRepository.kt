package com.ationet.androidterminal.core.domain.repository

interface PreAuthorizationRepository<E> {
    suspend fun create(preAuthorization: E): E
    suspend fun getPreAuthorization(id: Int): E?
    suspend fun getPreAuthorizationByAuthCode(authorizationCode: String): E?
    suspend fun getAllPreAuthorization(): List<E>
}