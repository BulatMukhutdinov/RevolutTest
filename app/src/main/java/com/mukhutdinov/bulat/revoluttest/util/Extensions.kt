package com.mukhutdinov.bulat.revoluttest.util

import android.content.Context
import android.widget.Toast

fun Context.showError(message: String?) {
    Toast.makeText(this, "Oops... an error has occurred. Details: $message", Toast.LENGTH_SHORT).show()
}