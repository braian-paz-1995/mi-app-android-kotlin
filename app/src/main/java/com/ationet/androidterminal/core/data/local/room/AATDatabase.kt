package com.ationet.androidterminal.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.CompletionEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.FusionCompletionEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.FusionPreAuthorizationEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.PreAuthorizationEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.StandAlonePreAuthorizationEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.FusionPumpLockEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.FusionSaleEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.SaleEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.void_transaction.VoidTransactionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation.FusionLoyaltyAccumulationEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation.LoyaltyAccumulationEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry.FusionLoyaltyBalanceEnquiryEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry.LoyaltyBalanceEnquiryEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_batch.LoyaltyBatchEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption.FusionLoyaltyPointsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption.LoyaltyPointsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption.FusionLoyaltyRewardsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption.LoyaltyRewardsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_void_transaction.LoyaltyVoidTransactionEntity
import com.ationet.androidterminal.core.data.local.room.entity.product.ProductEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptProductModifierEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptViewEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.batch.BatchEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC.FusionRechargeCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC.RechargeCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC.FusionReverseCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC.ReverseCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.tm.modules.ModulesCapabilityEntity
import com.ationet.androidterminal.core.data.local.room.entity.tm.payment_method.PaymentMethodEntity
import com.ationet.androidterminal.core.data.local.room.entity.transaction.TransactionView
import com.ationet.androidterminal.core.data.local.room.fleet.CompletionDao
import com.ationet.androidterminal.core.data.local.room.fleet.PreAuthorizationDao
import com.ationet.androidterminal.core.data.local.room.fleet.SaleDao
import com.ationet.androidterminal.core.data.local.room.fleet.VoidDao



@Database(
    entities = [
        ProductEntity::class,
        ReceiptEntity::class,
        ReceiptProductModifierEntity::class,
        PreAuthorizationEntity::class,
        FusionPreAuthorizationEntity::class,
        StandAlonePreAuthorizationEntity::class,
        CompletionEntity::class,
        FusionCompletionEntity::class,
        SaleEntity::class,
        FusionSaleEntity::class,
        RechargeCCEntity::class,
        FusionRechargeCCEntity::class,
        ReverseCCEntity::class,
        FusionReverseCCEntity::class,
        BatchEntity::class,
        LoyaltyBatchEntity::class,
        VoidTransactionEntity::class,
        LoyaltyVoidTransactionEntity::class,
        PaymentMethodEntity::class,
        ModulesCapabilityEntity::class,
        LoyaltyAccumulationEntity::class,
        FusionLoyaltyAccumulationEntity::class,
        LoyaltyBalanceEnquiryEntity::class,
        FusionLoyaltyBalanceEnquiryEntity::class,
        LoyaltyPointsRedemptionEntity::class,
        FusionLoyaltyPointsRedemptionEntity::class,
        LoyaltyRewardsRedemptionEntity::class,
        FusionLoyaltyRewardsRedemptionEntity::class,
        FusionPumpLockEntity::class
    ],
    views = [ReceiptViewEntity::class, TransactionView::class],
    version = 2,
    autoMigrations = [],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AATDatabase : RoomDatabase() {
    abstract val productDao: ProductDao

    /**
     * Access receipts
     * */
    abstract val receiptDao: ReceiptDao

    /**
     * Access pre authorizations
     * */
    abstract val preAuthorizationDao: PreAuthorizationDao

    /**
     * Completion's Data Access Object
     * */
    abstract val completionDao: CompletionDao

    /**
     * Sale's Data Access Object
     * */
    abstract val saleDao: SaleDao


    /**
     * Void's Data Access Object
     * */
    abstract val voidDao: VoidDao


    /**
     * Data Access Objets for common transaction data
     * */
    abstract val transactionDao: TransactionDao


    /**
     * Data Access Objets for common transaction data
     * */
    abstract val fusionPumpLockDao: FusionPumpLockDao
}