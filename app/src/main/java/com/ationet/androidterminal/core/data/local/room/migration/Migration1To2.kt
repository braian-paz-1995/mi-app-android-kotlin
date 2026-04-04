package com.ationet.androidterminal.core.data.local.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `fusion_pump_lock` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `fusion_sale_id` INTEGER NOT NULL,
                `terminal_id` TEXT NOT NULL,
                `status` TEXT NOT NULL,
                `updated_at` INTEGER NOT NULL
            )
        """.trimIndent())

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS 
            `index_fusion_pump_lock_fusion_sale_id`
            ON `fusion_pump_lock` (`fusion_sale_id`)
        """.trimIndent())

        database.execSQL("""
            CREATE INDEX IF NOT EXISTS 
            `index_fusion_pump_lock_terminal_id`
            ON `fusion_pump_lock` (`terminal_id`)
        """.trimIndent())
    }
}