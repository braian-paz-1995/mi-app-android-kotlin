package com.ationet.androidterminal.core.data.remote.ationet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentityData(

    @SerialName("EntryMethod")
    val entryMethod: String? = null,

    @SerialName("IdentityNumber")
    val identityNumber: String? = null,

    @SerialName("Country")
    val country: String? = null,

    @SerialName("FirstName")
    val firstName: String? = null,

    @SerialName("LastName")
    val lastName: String? = null,

    @SerialName("Sex")
    val sex: String? = null,

    @SerialName("BirthDate")
    val birthDate: String? = null,

    @SerialName("IssueDate")
    val issueDate: String? = null,

    @SerialName("ProcedureNumber")
    val procedureNumber: String? = null,

    @SerialName("Copy")
    val copy: String? = null
)
