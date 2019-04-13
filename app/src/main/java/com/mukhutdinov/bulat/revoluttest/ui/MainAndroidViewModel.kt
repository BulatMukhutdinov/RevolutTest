package com.mukhutdinov.bulat.revoluttest.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mukhutdinov.bulat.revoluttest.domain.CurrencyCalculator
import com.mukhutdinov.bulat.revoluttest.gateway.CurrenciesGateway
import com.mukhutdinov.bulat.revoluttest.model.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal

class MainAndroidViewModel(
    private val gateway: CurrenciesGateway,
    private val calculator: CurrencyCalculator
) : MainViewModel, ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    override val isUpToDate = MutableLiveData<Boolean>()

    override val currencies = MutableLiveData<Pair<List<Currency>, Boolean>>()

    override val error = MutableLiveData<String?>()

    init {
        compositeDisposable.add(gateway.observe()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnNext { isUpToDate.postValue(it.date.isNotEmpty()) }
            .map {
                mutableListOf<Currency>().apply {
                    add(gateway.baseCurrency())
                    it.rates.forEach { rate ->
                        add(Currency(rate.key).apply { value = BigDecimal(rate.value) })
                    }
                }
            }
            .flatMapSingle { calculator.onBaseUpdate(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { currencies.value = it to false },
                { error.value = it.message }
            )
        )
    }

    override fun onSelectedValueUpdate(newValue: String, shown: List<Currency>) {
        compositeDisposable.add(calculator.onSelectedValueUpdate(newValue, shown)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { currencies.value = it to true },
                { error.value = it.message }
            )
        )
    }

    override fun onSelectedUpdate(newPosition: Int, shown: List<Currency>) {
        compositeDisposable.add(calculator.onSelectedUpdate(newPosition, shown)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { currencies.value = it to false },
                { error.value = it.message }
            )
        )
    }

    @SuppressLint("CheckResult")
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()

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