package com.mukhutdinov.bulat.revoluttest.gateway

data class CurrenciesDto(
    val base: String,
    val date: String,
    val rates: Map<String, String>
)