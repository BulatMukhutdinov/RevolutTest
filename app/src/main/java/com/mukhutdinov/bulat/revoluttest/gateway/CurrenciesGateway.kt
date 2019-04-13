package com.mukhutdinov.bulat.revoluttest.gateway

import com.mukhutdinov.bulat.revoluttest.model.Currencies
import com.mukhutdinov.bulat.revoluttest.model.Currency
import io.reactivex.Completable
import io.reactivex.Flowable

interface CurrenciesGateway {

    fun baseCurrency(): Currency

    fun observe(): Flowable<Currencies>

    fun saveLast(): Completable
}