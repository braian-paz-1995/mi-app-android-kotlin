package com.ationet.androidterminal.core.domain.use_case.transactions

import android.util.Log
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.transaction.TransactionType
import com.ationet.androidterminal.core.domain.model.transaction.TransactionView
import com.ationet.androidterminal.core.domain.repository.LoyaltyVoidRepository
import com.ationet.androidterminal.core.domain.repository.TransactionRepository
import com.ationet.androidterminal.core.domain.repository.VoidRepository
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetVoidableTransactionByAuthorizationCodeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
    private val voidRepository: VoidRepository,
) {
    private val voidableTransactionTypes = listOf(TransactionType.Sale, TransactionType.Completion)

    sealed interface Result {
        data class Ok(val transaction: TransactionView): Result
        data object TransactionNotFound: Result
        data object InvalidTransactionType: Result
    }

    suspend operator fun invoke(
        authorizationCode: String,
        controllerType: Configuration.ControllerType
    ): Result {
        val lastOpenBatchId = getLastOpenBatchUseCase.invoke()?.id ?: 0

        val transaction = transactionRepository.getTransactionByAuthorizationCode(
            authorizationCode = authorizationCode,
            batchId = lastOpenBatchId,
        )
        val voidTransactionExistsException =
            voidRepository.getTransactionsByAuthorizationCode(AuthorizationCode = authorizationCode)

        if (voidTransactionExistsException){
            Log.w(
                TAG,
                "Void transaction: transaction with authorization code '$authorizationCode' and controller type '$controllerType' has already been voided."            )
            return Result.TransactionNotFound
        }
        if(transaction == null) {
            Log.w(TAG, "Void transaction: transaction not found with authorization code: '$authorizationCode' and controller type: '$controllerType'")
            return Result.TransactionNotFound
        }

        if(transaction.type !in voidableTransactionTypes) {
            Log.w(TAG, "Void transaction: transaction with authorization code: '$authorizationCode' and type: '${transaction.type}' is not voidable")
            return Result.InvalidTransactionType
        }

        Log.d(TAG, "Void transaction: transaction found with authorization code: '$authorizationCode'")
        return Result.Ok(transaction)
    }

    private companion object {
        private const val TAG: String = "GetVoidTransaction"
    }
}