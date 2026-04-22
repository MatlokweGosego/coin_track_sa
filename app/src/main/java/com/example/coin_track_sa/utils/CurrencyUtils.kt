package com.example.coin_track_sa.utils

import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    fun formatZAR(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("en", "ZA")).format(amount)
    }
}