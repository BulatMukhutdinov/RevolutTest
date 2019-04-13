package com.mukhutdinov.bulat.revoluttest.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mukhutdinov.bulat.revoluttest.model.Currency
import com.mukhutdinov.bulat.revoluttest.util.showError
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyAdapter(
    baseCurrency: Currency,
    private val context: Context
) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private val compositeDisposable = CompositeDisposable()

    private val shownCurrencies = mutableListOf<Currency>()

    private val baseCurrencies = mutableListOf<Currency>()

    private var selected: Currency = baseCurrency

    private var coefficient: BigDecimal = BigDecimal.ONE

    private var isBinding = false

    private val clickListener: (Int) -> Unit = {
        compositeDisposable.add(Single
            .fromCallable {
                selected = baseCurrencies[it]

                val baseSorted = sort(baseCurrencies)
                baseCurrencies.clear()
                baseCurrencies.addAll(baseSorted)

                val sorted = sort(shownCurrencies)
                sorted
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    dispatchUpdates(shownCurrencies, it)
                },
                {
                    context.showError(it.message)
                }
            )
        )
    }

    private val selectedValueChangeListener: (String) -> Unit = {
        compositeDisposable.add(Single
            .fromCallable {
                coefficient = BigDecimal(it).divide(selected.value, SCALE, RoundingMode.CEILING)

                val copy = copy(baseCurrencies)
                adjust(copy, BigDecimal(it))

                shownCurrencies[0] = selected.copy().apply { value = BigDecimal(it) }

                copy
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    dispatchUpdates(shownCurrencies, it)
                },
                {
                    context.showError(it.message)
                }
            )
        )
    }

    fun updateCurrencies(updated: List<Currency>) {
        compositeDisposable.add(Single
            .fromCallable {
                baseCurrencies.clear()
                baseCurrencies.addAll(sort(updated))

                if (baseCurrencies.isNotEmpty()) {
                    coefficient =
                        (coefficient * selected.value.divide(baseCurrencies[0].value, SCALE, RoundingMode.CEILING))
                            .stripTrailingZeros()

                    coefficient = coefficient.setScale(SCALE, RoundingMode.CEILING)
                    selected = baseCurrencies[0]
                }

                val sortedCopy = copy(sort(updated))
                if (shownCurrencies.isNotEmpty()) {
                    adjust(sortedCopy, shownCurrencies[0].value)
                }
                sortedCopy
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    dispatchUpdates(shownCurrencies, it)
                },
                {
                    context.showError(it.message)
                }
            )
        )
    }

    fun onStop() {
        compositeDisposable.clear()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }

    private fun copy(original: List<Currency>) = original.map { it.copy().apply { value = it.value } }

    private fun adjust(list: List<Currency>, valueOfFirst: BigDecimal) {
        list.forEachIndexed { index, item ->
            if (index == 0) item.value = valueOfFirst
            else item.value *= coefficient
        }
    }

    private fun dispatchUpdates(oldValues: MutableList<Currency>, newValues: List<Currency>) {
        val userDiffUtilCallback = DiffUtilCallback(oldValues, newValues)
        val userDiffResult = DiffUtil.calculateDiff(userDiffUtilCallback)

        oldValues.clear()
        oldValues.addAll(newValues)

        if (!isBinding) {
            userDiffResult.dispatchUpdatesTo(this)
        }
    }

    private fun sort(list: List<Currency>): List<Currency> {
        if (list.isEmpty()) return list

        val sorted = mutableListOf<Currency>().apply {
            addAll(list.sortedBy { it.name })
        }

        val c = sorted.removeAt(sorted.indexOf(selected))
        sorted.add(0, c)

        return sorted
    }

    override fun getItemCount(): Int =
        shownCurrencies.size

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        isBinding = true
        holder.bindTo(shownCurrencies[position])
        isBinding = false
    }

    override fun onViewRecycled(holder: CurrencyViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder.create(parent, clickListener, selectedValueChangeListener)

    override fun getItemId(position: Int): Long {
        return shownCurrencies[position].name.hashCode().toLong()
    }

    companion object {
        private const val SCALE = 10
    }
}