package com.mukhutdinov.bulat.revoluttest.model

import java.math.BigDecimal

data class Currency(val name: String) {
    var value: BigDecimal = BigDecimal.ZERO
}