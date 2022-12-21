package com.example.myaccountbook

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myaccountbook.data.ExRecord
import com.example.myaccountbook.data.InRecord
import com.example.myaccountbook.databinding.ActivityIncomeBinding
import com.example.myaccountbook.databinding.ActivitySpendBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class IncomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityIncomeBinding
    lateinit var rdb: DatabaseReference

    private var day=""
    private var time=""
    private var nSec = SimpleDateFormat("ss").format(Date(System.currentTimeMillis()))
    private var incomeTitle=""
    private var incomeDate=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.incomeDate.setOnClickListener{
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->
                if (month<9 && dayOfMonth<10) day = "${year}-0${month+1}-0${dayOfMonth}"
                else if (month<9) day = "${year}-0${month+1}-${dayOfMonth}"
                else if (dayOfMonth<10) day = "${year}-${month+1}-0${dayOfMonth}"
                else day = "${year}-${month}-${dayOfMonth}"
                binding.incomeDate.setText(day)
            }
            DatePickerDialog(binding.incomeDate.context, dateSetListener, cal.get(Calendar.YEAR), cal.get(
                Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.incomeTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                if(hourOfDay< 10 && minute<10) time = "0${hourOfDay}:0${minute}:${nSec}"
                else if(hourOfDay<10) time = "0${hourOfDay}:${minute}:${nSec}"
                else if(minute<10) time = "${hourOfDay}:0${minute}:${nSec}"
                else time = "${hourOfDay}:${minute}:${nSec}"
                binding.incomeTime.setText(time)
            }
            TimePickerDialog(binding.incomeTime.context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),true).show()
        }

        setupEditMode()
        binding.btnClose.setOnClickListener{
            finish()
        }
    }

    private fun setupEditMode() {
        binding.incomeExplain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                incomeTitle = binding.incomeExplain.text.toString()
            }
        })
//
        val incomeAmount = binding.incomeAmount.text ?: ""
        binding.btnIncomeSubmit.setOnClickListener {
            incomeDate = "${binding.incomeDate.text.toString()} ${binding.incomeTime.text.toString()}"

            var rdb1 = Firebase.database.getReference("AutoAssendingKey")
            rdb1.get().addOnSuccessListener {
                var autoAssendingKey = it.value.toString().toInt()
                rdb1.setValue(autoAssendingKey+1)
                rdb = Firebase.database.getReference("Record")
                val item = InRecord(
                    incomeDate,
                    incomeAmount.toString().toInt(),
                    incomeTitle
                )
                rdb.child("income").child(autoAssendingKey.toString()).setValue(item)
            }
            finish()
        }
    }


}