package com.mukhutdinov.bulat.revoluttest.domain

import com.mukhutdinov.bulat.revoluttest.model.Currency
import io.reactivex.Single
import java.math.BigDecimal
import java.math.RoundingMode

class RevolutCurrencyCalculator(baseCurrency: Currency) : CurrencyCalculator {

    private var coefficient: BigDecimal = BigDecimal.ONE

    private val baseCurrencies = mutableListOf<Currency>()

    private var baseSelected: Currency = baseCurrency

    private var shownSelected: Currency = baseCurrency

    override fun onSelectedValueUpdate(newValue: String, shown: List<Currency>): Single<List<Currency>> =
        Single.fromCallable {
            shownSelected.value = BigDecimal(newValue)
            coefficient = shownSelected.value.divide(baseSelected.value, SCALE, RoundingMode.CEILING)

            val copy = copy(baseCurrencies)
            adjust(copy, shownSelected.value)

            copy
        }

    override fun onSelectedUpdate(newPosition: Int, shown: List<Currency>): Single<List<Currency>> =
        Single.fromCallable {
            baseSelected = baseCurrencies[newPosition]
            shownSelected = shown[newPosition]

            val baseSorted = sort(baseCurrencies)
            baseCurrencies.clear()
            baseCurrencies.addAll(baseSorted)

            val sorted = sort(shown)
            sorted
        }

    override fun onBaseUpdate(base: List<Currency>): Single<List<Currency>> =
        Single.fromCallable {
            baseCurrencies.clear()
            baseCurrencies.addAll(sort(base))

            if (baseCurrencies.isNotEmpty()) {
                coefficient =
                    (coefficient * baseSelected.value.divide(baseCurrencies[0].value, SCALE, RoundingMode.CEILING))
                        .stripTrailingZeros()

                coefficient = coefficient.setScale(SCALE, RoundingMode.CEILING)
                baseSelected = baseCurrencies[0]
            }

            val sortedCopy = copy(sort(base))

            adjust(sortedCopy, shownSelected.value)

            sortedCopy
        }


    private fun adjust(list: List<Currency>, valueOfFirst: BigDecimal) {
        list.forEachIndexed { index, item ->
            if (index == 0) item.value = valueOfFirst
            else item.value *= coefficient

            item.value = item.value.stripTrailingZeros()
        }
    }

    private fun copy(original: List<Currency>) = original.map { it.copy().apply { value = it.value } }

    private fun sort(list: List<Currency>): List<Currency> {
        if (list.isEmpty()) return list

        val sorted = mutableListOf<Currency>().apply {
            addAll(list.sortedBy { it.name })
        }

        val c = sorted.removeAt(sorted.indexOf(baseSelected))
        sorted.add(0, c)

        return sorted
    }

    companion object {
        private const val SCALE = 10
    }
}