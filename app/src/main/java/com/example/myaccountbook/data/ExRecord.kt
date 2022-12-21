package com.example.myaccountbook.data

import java.text.SimpleDateFormat
import java.util.*

data class ExRecord(var rDate: String, var rCategory: String, var rAmount: Int, var rMemo: String) {
    constructor():this(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date(System.currentTimeMillis())), "기타", 0, "")
}