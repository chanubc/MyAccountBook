package com.example.myaccountbook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.myaccountbook.databinding.FragmentBudgetBinding
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class BudgetFragment : Fragment() {
    lateinit var rdb: DatabaseReference //데이터 베이스
    lateinit var binding: FragmentBudgetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBudgetBinding.inflate(layoutInflater, container, false )
        getTodayData()
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val keySet = this.arguments?.keySet()
        binding!!.apply {
          Intent(activity, BudgetRecordActivity::class.java) .apply {
              btnBudgetInput.setOnClickListener {
                  startActivity(this)
              }
              //tvDetailRemain.text = this.getStringExtra("budgetAmount")
          }
          btnSpendRecord.setOnClickListener {
              val intent = Intent(activity, SpendActivity::class.java)
              startActivity(intent)
          }
          btnImportRecord.setOnClickListener{
              val intent = Intent(activity, IncomeActivity::class.java)
              startActivity(intent)
          }
          if(keySet != null) {
              if("totalbudget" in keySet) tvTotalBudget.text = arguments?.getString("totalbudget") + "원"
              if("spendSuggest" in keySet) tvSpendSuggest.text = arguments?.getString("spendSuggest") + "원"
              if("spendAmount" in keySet) tvDetailRemain.text = arguments?.getString("spendAmount") + "원"
          }
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

    private fun getTodayData() {
        rdb = Firebase.database.getReference("Budget")
        // 날짜,시간
        var currentYearandMonth = SimpleDateFormat("yyyy-mm").format(Date(System.currentTimeMillis()))
        var year = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        var month = SimpleDateFormat("mm").format(Date(System.currentTimeMillis()))
        //val query = rdb.child("budget")

        var getdata = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.getChildren()) {
                    //firebase에 직접 접근
//                    var path = i.childrenCount
                    if(i.key == currentYearandMonth){
                        var todayBudget = i.child("ramount").getValue().toString()
                        var spendSuggest = (todayBudget.toInt() / getDaysInMonth(month.toInt(), year.toInt())).toString()
                        binding.tvTotalBudget.text = todayBudget
                        binding.tvSpendSuggest.text = spendSuggest
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }


        }
        rdb.addListenerForSingleValueEvent(getdata)
    }
}
