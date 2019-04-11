package com.mukhutdinov.bulat.revoluttest.model

data class Currencies(
    val base: String,
    val date: String,
    val rates: Map<String, String>
)