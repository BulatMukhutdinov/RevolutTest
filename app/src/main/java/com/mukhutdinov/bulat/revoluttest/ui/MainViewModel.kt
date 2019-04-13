package com.mukhutdinov.bulat.revoluttest.ui

import androidx.lifecycle.LiveData
import com.mukhutdinov.bulat.revoluttest.model.Currency

interface MainViewModel {

    val baseCurrency: Currency

    val currencies: LiveData<List<Currency>>

    val isUpToDate: LiveData<Boolean>

    val error: LiveData<String?>
}