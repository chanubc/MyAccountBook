package com.example.myaccountbook.data

import java.text.SimpleDateFormat
import java.util.*

data class InRecord(var rDate: String, var rAmount: Int, var rMemo: String) {
    constructor():this(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date(System.currentTimeMillis())), 0, "")
}
