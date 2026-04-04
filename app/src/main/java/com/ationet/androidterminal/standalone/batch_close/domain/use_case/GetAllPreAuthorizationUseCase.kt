package com.ationet.androidterminal.standalone.batch_close.domain.use_case

import com.atio.log.Logger
import com.atio.log.util.d
import com.ationet.androidterminal.core.domain.model.preauthorization.PreAuthorizationStandalone
import com.ationet.androidterminal.core.domain.repository.PreAuthorizationRepository
import javax.inject.Inject

class GetAllPreAuthorizationUseCase @Inject constructor(
    private val preAuthorizationRepository: PreAuthorizationRepository<PreAuthorizationStandalone>
) {
    companion object {
        private val logger = Logger("GetAllPreAuthorizationUseCase")
    }

    suspend operator fun invoke(): List<PreAuthorizationStandalone> {
        logger.d("Getting all pre-authorizations")
        val result = preAuthorizationRepository.getAllPreAuthorization()
        logger.d("Pre-authorizations retrieved: $result")
        return result
    }
}