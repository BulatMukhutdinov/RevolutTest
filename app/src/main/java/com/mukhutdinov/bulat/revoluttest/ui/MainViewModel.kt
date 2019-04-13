package com.mukhutdinov.bulat.revoluttest.ui

import androidx.lifecycle.LiveData
import com.mukhutdinov.bulat.revoluttest.model.Currency

interface MainViewModel {

    val currencies: LiveData<Pair<List<Currency>, Boolean>>

    val isUpToDate: LiveData<Boolean>

    val error: LiveData<String?>

    fun onSelectedValueUpdate(newValue: String, shown: List<Currency>)

    fun onSelectedUpdate(newPosition: Int, shown: List<Currency>)
}