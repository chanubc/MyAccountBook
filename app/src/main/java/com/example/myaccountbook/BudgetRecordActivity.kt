package com.example.myaccountbook

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.myaccountbook.data.MyBudget
import com.example.myaccountbook.databinding.ActivityBudgetRecordBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class BudgetRecordActivity : AppCompatActivity() {
    lateinit var binding: ActivityBudgetRecordBinding
    lateinit var rdb: DatabaseReference

    val Date: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.budgetDate.setText(Date.toString())

        binding.btnBudgetSetting.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val bundle = Bundle()

            var rdb = Firebase.database.getReference("Budget")
            val budgetAmount = binding.budgetAmount.text.toString().toInt()
            val budgetDate = binding.budgetDate.text
            val (year, month, day) = budgetDate.toString().split("-")
            val spendSuggest = (budgetAmount / getDaysInMonth(month.toInt(), year.toInt())).toString()

            val item = MyBudget(budgetAmount)
            rdb.child("${year}-${month}").setValue(item)

            bundle.putString("totalbudget", binding.budgetAmount.text.toString())
            intent.putExtra("totalbudget", bundle)
            bundle.putString("spendSuggest", spendSuggest)
            intent.putExtra("spendSuggest", bundle)

            startActivity(intent)
        }
        binding.btnClose.setOnClickListener{
            finish()
        }
    }



    fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28 // 윤년계산
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }

}