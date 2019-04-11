package com.mukhutdinov.bulat.revoluttest.gateway

import com.mukhutdinov.bulat.revoluttest.model.Currencies
import io.reactivex.Completable
import io.reactivex.Flowable

interface CurrenciesGateway {

    val baseCurrency: String

    fun observe(): Flowable<Currencies>

    fun saveLast(): Completable
}