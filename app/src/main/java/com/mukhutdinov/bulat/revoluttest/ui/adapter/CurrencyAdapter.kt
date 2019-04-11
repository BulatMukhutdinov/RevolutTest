package com.mukhutdinov.bulat.revoluttest.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mukhutdinov.bulat.revoluttest.model.Currency
import java.math.BigDecimal

class CurrencyAdapter(baseCurrency: Currency) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private val shownCurrencies = mutableListOf<Currency>()

    private val baseCurrencies = mutableListOf<Currency>()

    private var selected: Currency = baseCurrency

    private var coefficient: BigDecimal = BigDecimal.ONE

    private val clickListener: (Int) -> Unit = {
        selected = baseCurrencies[it]

        val baseSorted = sort(baseCurrencies)
        baseCurrencies.clear()
        baseCurrencies.addAll(baseSorted)

        val sorted = sort(shownCurrencies)
        dispatchUpdates(shownCurrencies, sorted.toMutableList())
    }

    private val selectedValueChangeListener: (String) -> Unit = {
        coefficient = BigDecimal(it) / selected.value

        val copy = baseCurrencies.map { it.copy().apply { value = it.value } }

        copy.forEachIndexed { index, item ->
            if (index == 0) item.value = BigDecimal(it)
            else item.value *= coefficient
        }

        shownCurrencies[0] = selected.copy().apply { value = BigDecimal(it) }

        dispatchUpdates(shownCurrencies, copy.toMutableList())
    }

    fun updateCurrencies(updated: List<Currency>) {
        baseCurrencies.clear()
        baseCurrencies.addAll(sort(updated))

        val sorted = sort(updated).map { it.copy().apply { value = it.value } }
        dispatchUpdates(shownCurrencies, sorted.toMutableList())
        shownCurrencies.clear()
        shownCurrencies.addAll(sorted)
    }

    private fun dispatchUpdates(oldValues: MutableList<Currency>, newValues: List<Currency>) {
        val userDiffUtilCallback = DiffUtilCallback(oldValues, newValues)
        val userDiffResult = DiffUtil.calculateDiff(userDiffUtilCallback)

        oldValues.clear()
        oldValues.addAll(newValues)

        userDiffResult.dispatchUpdatesTo(this)
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
        holder.bindTo(shownCurrencies[position])
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
}