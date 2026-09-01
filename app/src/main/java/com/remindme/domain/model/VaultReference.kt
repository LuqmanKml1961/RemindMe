package com.remindme.domain.model

enum class VaultCategory(val label: String) {
    PEOPLE("People & Profiles"),
    HOME_VEHICLE("Home & Vehicle"),
    PROPERTY("Property & Access")
}

data class VaultReference(
    val id: Long = 0,
    val category: VaultCategory = VaultCategory.PEOPLE,
    val title: String = "",
    val note: String = "",
    val createdAt: Long = 0L
)