package com.ationet.androidterminal.standalone.completion.domain.use_cases

import android.util.Log
import com.ationet.androidterminal.core.data.local.preauthorization.PreAuthorizationStandAloneRepository
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationStandalone
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPreAuthorizationByIdentifier @Inject constructor(
    private val preAuthorizationRepository: PreAuthorizationStandAloneRepository
) {
    suspend operator fun invoke(
        identification: String,
    ): PreAuthorizationByIdentifierResult {
        val preAuth = try {
            preAuthorizationRepository.getPreAuthorizationByIdentification(identification)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()

            Log.e(TAG, "Pre-authorization query failed. Identification: '$identification'", e)
            return PreAuthorizationByIdentifierResult.Error
        }

        if(preAuth == null) {
            Log.w(TAG, "Pre-authorization by identification: '$identification' not found")
            return PreAuthorizationByIdentifierResult.NotFound
        }

        return PreAuthorizationByIdentifierResult.PreAuthorizationFound(
            preAuthorization = preAuth
        )
    }

    private companion object {
        private const val TAG: String = "GetPreAuthByIdentifier"
    }
}

sealed interface PreAuthorizationByIdentifierResult {
    data object NotFound: PreAuthorizationByIdentifierResult
    data class PreAuthorizationFound(
        val preAuthorization: PreAuthorizationStandalone
    ): PreAuthorizationByIdentifierResult
    data object Error: PreAuthorizationByIdentifierResult
}