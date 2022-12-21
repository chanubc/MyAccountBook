package com.example.myaccountbook

import java.text.SimpleDateFormat
import java.util.*

class InRecord(var rDate: String, var rAmount: Int, var rMemo: String) {
    constructor():this(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date(System.currentTimeMillis())), 0, "")
}