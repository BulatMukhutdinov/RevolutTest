package com.mukhutdinov.bulat.revoluttest.util

import android.content.Context
import android.widget.Toast

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun Context.showError(message: String?) {
    Toast.makeText(this, "Oops... an error has occurred. Details: $message", Toast.LENGTH_SHORT).show()
}