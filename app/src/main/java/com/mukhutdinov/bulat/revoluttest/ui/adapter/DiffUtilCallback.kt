package com.mukhutdinov.bulat.revoluttest.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mukhutdinov.bulat.revoluttest.model.Currency

class DiffUtilCallback(private val oldList: List<Currency>, private val newList: List<Currency>)
    : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.name == newUser.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.value == newUser.value
    }
}