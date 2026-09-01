package com.remindme.domain.model

data class Medication(
    val id: Long = 0,
    val name: String = "",
    val dosage: String = "",
    val instructions: String = ""
)