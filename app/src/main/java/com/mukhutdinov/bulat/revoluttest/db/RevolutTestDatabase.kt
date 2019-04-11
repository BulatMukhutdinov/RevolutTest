package com.mukhutdinov.bulat.revoluttest.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CurrencyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RevolutTestDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
}