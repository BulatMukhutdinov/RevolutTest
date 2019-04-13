package com.mukhutdinov.bulat.revoluttest.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mukhutdinov.bulat.revoluttest.gateway.CurrenciesGateway
import com.mukhutdinov.bulat.revoluttest.model.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal

class MainAndroidViewModel(private val gateway: CurrenciesGateway) : MainViewModel, ViewModel() {

    override val baseCurrency: Currency = Currency(gateway.baseCurrency).apply { value = BigDecimal.ONE }

    override val isUpToDate = MutableLiveData<Boolean>()

    override val currencies = MutableLiveData<List<Currency>>()

    override val error = MutableLiveData<String?>()

    private val disposable: Disposable

    init {
        disposable = gateway.observe()
            .observeOn(Schedulers.computation())
            .doOnNext { isUpToDate.postValue(it.date.isNotEmpty()) }
            .map {
                mutableListOf<Currency>().apply {
                    add(baseCurrency)
                    it.rates.forEach { rate ->
                        add(Currency(rate.key).apply { value = BigDecimal(rate.value) })
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { currencies.value = it },
                { error.value = it.message }
            )
    }

    @SuppressLint("CheckResult")
    override fun onCleared() {
        super.onCleared()
        disposable.dispose()

        gateway.saveLast()
            .subscribe(
                {
                    // successfully saved to db
                },
                {
                    // failed to store last fetched currencies
                }
            )
    }
}