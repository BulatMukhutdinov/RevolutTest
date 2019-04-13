package com.mukhutdinov.bulat.revoluttest.di

import androidx.room.Room
import com.github.kittinunf.fuel.core.FuelManager
import com.mukhutdinov.bulat.revoluttest.db.RevolutTestDatabase
import com.mukhutdinov.bulat.revoluttest.domain.CurrencyCalculator
import com.mukhutdinov.bulat.revoluttest.domain.RevolutCurrencyCalculator
import com.mukhutdinov.bulat.revoluttest.gateway.CurrenciesBoundedGateway
import com.mukhutdinov.bulat.revoluttest.gateway.CurrenciesGateway
import com.mukhutdinov.bulat.revoluttest.ui.MainAndroidViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object InjectionModule {
    private const val DATABASE_NAME = "revolut_test_db"

    val module = module {

        viewModel {
            MainAndroidViewModel(get(), get())
        }

        factory<CurrencyCalculator> {
            RevolutCurrencyCalculator(get<CurrenciesGateway>().baseCurrency())
        }

        single<CurrenciesGateway> {
            CurrenciesBoundedGateway(get(), get())
        }

        single {
            FuelManager().apply {
                basePath = "https://revolut.duckdns.org"
            }
        }

        single {
            Room.databaseBuilder(get(), RevolutTestDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }

        single {
            get<RevolutTestDatabase>().currencyDao()
        }
    }
}