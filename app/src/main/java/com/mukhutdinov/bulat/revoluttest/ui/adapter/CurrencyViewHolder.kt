package com.mukhutdinov.bulat.revoluttest.ui.adapter

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.mukhutdinov.bulat.revoluttest.R
import com.mukhutdinov.bulat.revoluttest.model.Currency
import kotlinx.android.synthetic.main.currency_item.view.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*


class CurrencyViewHolder(
    view: View,
    private val clickListener: (Int) -> Unit,
    private val valueChangeListener: (String) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val currency = itemView.currency
    private val amount = itemView.amount
    private lateinit var textWatcher: TextWatcher
    private val decimalFormat = NumberFormat.getInstance(Locale.US)

    fun bindTo(item: Currency) {
        currency.text = item.name
        amount.setText(format(item.value))
        amount.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) clickListener(adapterPosition) }

        textWatcher = amount.doOnTextChanged { text, _, _, _ ->
            if (adapterPosition == 0 && text != null) {
                if (text.isEmpty()) valueChangeListener("0")
                else valueChangeListener(text.toString())
            }
        }
    }

    private fun format(value: BigDecimal): String {
        val decimal = value.setScale(2, BigDecimal.ROUND_DOWN)
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.minimumFractionDigits = 0
        decimalFormat.isGroupingUsed = false
        return decimalFormat.format(decimal)
    }

    fun onRecycled() {
        if (::textWatcher.isInitialized) {
            amount.removeTextChangedListener(textWatcher)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            clickListener: (Int) -> Unit,
            valueChangeListener: (String) -> Unit
        ): CurrencyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
            return CurrencyViewHolder(view, clickListener, valueChangeListener)
        }
    }
}