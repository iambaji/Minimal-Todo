package com.example.avjindersinghsekhon.minimaltodo.utility

import java.text.SimpleDateFormat
import java.util.*

fun String.formatDate(formatString: String?, dateToFormat: Date?): String {
    val simpleDateFormat = SimpleDateFormat(formatString)
    return simpleDateFormat.format(dateToFormat)
}
