package com.example.myaccountbook

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.bind
import android.system.Os.close
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myaccountbook.data.ExRecord
import com.example.myaccountbook.databinding.ActivityMainBinding
import com.example.myaccountbook.databinding.ActivitySpendBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.round
import kotlin.properties.Delegates

class SpendActivity : AppCompatActivity() {
    lateinit var binding : ActivitySpendBinding
    lateinit var rdb: DatabaseReference

    var categoryList = ArrayList<MyCategory>().apply {
        for (item in SpendCategoryEnum.values()) {
            add(MyCategory(item.defaultRes, item.title, false))
        }
    }

    var defaultCategoryList = ArrayList<MyCategory>().apply {
        for (item in SpendCategoryEnum.values()){
            add(MyCategory(item.defaultRes, item.title, false))
        }
    }
    var selectedCategoryList = ArrayList<MyCategory>().apply {
        for (item in SpendCategoryEnum.values()) {
            add(MyCategory(item.selectedRes, item.title, true))
        }
    }

    private var day=""
    private var time=""
    private var nSec = SimpleDateFormat("ss").format(Date(System.currentTimeMillis()))
    private var paymentTitle=""
    private var paymentDate=""
    private var paymentCategory=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spendDate.setOnClickListener{
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->
                if (month<9 && dayOfMonth<10) day = "${year}-0${month+1}-0${dayOfMonth}"
                else if (month<9) day = "${year}-0${month+1}-${dayOfMonth}"
                else if (dayOfMonth<10) day = "${year}-${month+1}-0${dayOfMonth}"
                else day = "${year}-${month}-${dayOfMonth}"
                binding.spendDate.setText(day)
            }
            DatePickerDialog(binding.spendDate.context, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.spendTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                if(hourOfDay< 10 && minute<10) time = "0${hourOfDay}:0${minute}:${nSec}"
                else if(hourOfDay<10) time = "0${hourOfDay}:${minute}:${nSec}"
                else if(minute<10) time = "${hourOfDay}:0${minute}:${nSec}"
                else time = "${hourOfDay}:${minute}:${nSec}"
                binding.spendTime.setText(time)
            }
            TimePickerDialog(binding.spendTime.context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),true).show()
        }

        initRecyclerview()
        setupEditMode()
        binding.btnClose.setOnClickListener{
            finish()
        }
   }


    private fun initRecyclerview() {
        binding.category.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        binding.category.adapter = SpendCategoryAdapter(categoryList).apply {
            setItemClickListener(object : SpendCategoryAdapter.OnItemClickListener{
                override fun onClick(v: View, position: Int) {
                    paymentCategory = "${categoryList[position].title}"
                    (!categoryList[position].isSelect).also { categoryList[position].isSelect = it }
                    var count = 0
                    for(i in categoryList){
                        if(i.isSelect) count++
                    }
                    if(count > 1){
                        for ( i in categoryList ){
                            if (i!=categoryList[position]) i.isSelect = false
                        }
                    }

                    if(categoryList[position].isSelect) categoryList[position].icon = selectedCategoryList[position].icon
                    else categoryList[position].icon = defaultCategoryList[position].icon


                    notifyDataSetChanged()

                }
            })
            }
    }

    private fun setupEditMode() {
        binding.spendExplain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                paymentTitle = binding.spendExplain.text.toString()
            }
        })
//
        val paymentAmount = binding.spendAmount.text ?: ""
        binding.btnRecordSubmit.setOnClickListener {
            paymentDate = "${binding.spendDate.text.toString()} ${binding.spendTime.text.toString()}"

            var rdb1 = Firebase.database.getReference("AutoAssendingKey")
            rdb1.get().addOnSuccessListener {
                var autoAssendingKey = it.value.toString().toInt()
                rdb1.setValue(autoAssendingKey+1)
                rdb = Firebase.database.getReference("Record")
                val item = ExRecord(
                    paymentDate,
                    paymentCategory,
                    paymentAmount.toString().toInt(),
                    paymentTitle
                )
                rdb.child("expenses").child(autoAssendingKey.toString()).setValue(item)
            }
            finish()
        }
    }


}
