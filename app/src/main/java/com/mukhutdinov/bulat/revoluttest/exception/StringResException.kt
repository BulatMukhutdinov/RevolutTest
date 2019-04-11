package com.mukhutdinov.bulat.revoluttest.exception

import androidx.annotation.StringRes

class StringResException(cause: Throwable, @StringRes stringId: Int) : Exception(cause)