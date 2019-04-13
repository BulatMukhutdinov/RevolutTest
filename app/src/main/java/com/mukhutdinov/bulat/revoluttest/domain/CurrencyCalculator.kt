package com.mukhutdinov.bulat.revoluttest.domain

import com.mukhutdinov.bulat.revoluttest.model.Currency
import io.reactivex.Single

interface CurrencyCalculator {

    fun onSelectedValueUpdate(newValue: String, shown: List<Currency>): Single<List<Currency>>

    fun onSelectedUpdate(newPosition: Int, shown: List<Currency>): Single<List<Currency>>

    fun onBaseUpdate(base: List<Currency>): Single<List<Currency>>
}