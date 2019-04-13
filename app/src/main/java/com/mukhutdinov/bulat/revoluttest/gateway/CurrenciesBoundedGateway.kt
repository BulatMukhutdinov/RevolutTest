package com.mukhutdinov.bulat.revoluttest.gateway

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.gson.responseObject
import com.mukhutdinov.bulat.revoluttest.db.CurrencyDao
import com.mukhutdinov.bulat.revoluttest.db.CurrencyEntity
import com.mukhutdinov.bulat.revoluttest.model.Currencies
import com.mukhutdinov.bulat.revoluttest.model.Currency
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class CurrenciesBoundedGateway(
    private val currencyDao: CurrencyDao,
    private val fuelManager: FuelManager
) : CurrenciesGateway {

    private var currencies: Currencies? = null

    override fun baseCurrency(): Currency = Currency("EUR").apply { value = BigDecimal.ONE }

    override fun observe(): Flowable<Currencies> =
        fetchFromRemote()
            .flatMap { saveIfDbIsEmpty(it) }
            .onErrorResumeNext { fetchFromLocal() }
            .doOnSuccess { currencies = it }
            .repeatWhen { it.delay(UPDATE_RATE, TimeUnit.SECONDS) }
            .startWith {
                it.onNext(fetchFromLocal().blockingGet())
                it.onComplete()
            }

    private fun saveIfDbIsEmpty(currencies: Currencies): Single<Currencies> =
        if (currencyDao.getAll().isEmpty()) {
            saveLast()
                .toSingle { currencies }
        } else {
            Single.just(currencies)
        }

    private fun fetchFromRemote(): Single<Currencies> =
        Single.fromCallable {
            val (_, _, result) = fuelManager.get("latest", listOf("base" to baseCurrency().name))
                .responseObject<CurrenciesDto>()

            val (response, error) = result

            if (response != null) {
                Currencies(
                    base = response.base,
                    rates = response.rates,
                    date = response.date
                )
            } else {
                throw error?.exception ?: RuntimeException(error?.message)
            }
        }

    private fun fetchFromLocal(): Single<Currencies> =
        Single.fromCallable {
            val rates = mutableMapOf<String, String>()
            var base = ""

            currencyDao.getAll()
                .forEach {
                    if (it.isBase) {
                        base = it.name
                    } else {
                        rates[it.name] = it.value
                    }
                }

            Currencies(
                base = base,
                date = "",
                rates = rates
            )
        }

    override fun saveLast(): Completable =
        Completable.fromAction {
            currencies?.apply {
                val entities = mutableListOf<CurrencyEntity>()

                entities.add(
                    CurrencyEntity(
                        name = base,
                        isBase = true,
                        value = "1"
                    )
                )

                rates.forEach {
                    entities.add(
                        CurrencyEntity(
                            name = it.key,
                            isBase = false,
                            value = it.value
                        )
                    )
                }

                currencyDao.insertAll(entities)
            }
        }

    companion object {
        private const val UPDATE_RATE = 10L
    }
}