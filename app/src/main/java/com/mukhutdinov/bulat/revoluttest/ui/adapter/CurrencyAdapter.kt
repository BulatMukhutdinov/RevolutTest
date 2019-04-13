package com.mukhutdinov.bulat.revoluttest.ui.adapter

import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mukhutdinov.bulat.revoluttest.model.Currency

class CurrencyAdapter(
    private val clickListener: (Int) -> Unit,
    private val selectedValueChangeListener: (String) -> Unit
) : RecyclerView.Adapter<CurrencyViewHolder>() {

    val shownCurrencies = mutableListOf<Currency>()

    private var isBinding = false

    @MainThread
    fun updateCurrencies(updated: List<Currency>, shouldUpdateFirst: Boolean) {
        if (shownCurrencies.isNotEmpty() && shouldUpdateFirst) {
            shownCurrencies[0] = updated[0].copy().apply { value = updated[0].value }
        }

        dispatchUpdates(shownCurrencies, updated)
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
}