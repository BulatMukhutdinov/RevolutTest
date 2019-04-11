package com.mukhutdinov.bulat.revoluttest.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mukhutdinov.bulat.revoluttest.db.CurrencyEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
class CurrencyEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    val name: String,
    @ColumnInfo(name = COLUMN_VALUE)
    val value: String,
    @ColumnInfo(name = COLUMN_IS_BASE)
    val isBase: Boolean
) {
    companion object {
        const val TABLE_NAME = "currency"
        const val COLUMN_ID = "name"
        const val COLUMN_VALUE = "value"
        const val COLUMN_IS_BASE = "isBase"
    }
}
