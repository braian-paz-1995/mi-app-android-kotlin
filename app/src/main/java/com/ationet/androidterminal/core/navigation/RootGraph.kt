package com.ationet.androidterminal.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object MaintenanceGraph

sealed interface FleetGraph {
    @Serializable
    data object StandAlone : FleetGraph

    @Serializable
    data object Fusion : FleetGraph
}

@Serializable
object TaskGraph

@Serializable
object LoyaltyGraph
