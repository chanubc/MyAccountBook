package com.example.myaccountbook

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myaccountbook.databinding.FragmentChallengeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import java.util.*
import kotlin.collections.ArrayList


class ChallengeFragment : Fragment() {
    // param1 으로 들어오는 걸 fragment 에 붙여줄 거임
    // 1) 바인딩 변수 생성
    private var _binding: FragmentChallengeBinding?=null
    private val binding get() = _binding!!

    // 지출 기록 data 객체 타입으로 담아줄 거임
    var items = arrayListOf<String>()
    val database = Firebase.database
    val myRef = database.getReference("Record")
    val myRef2 = database.getReference("Budget")
    var total_final = 0

    lateinit var adapter2 : MyCalAdapter

    // context 를 MainActivity 로 캐스팅
    lateinit var mainActivity : MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //initRecyclerView()
        // Inflate the layout for this fragment
        // 2) 바인딩 변수로 화면에 view 전달
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    //
    override fun onDestroyView() {
        // fragment 가 깨진 경우 바인딩은 null 로
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 달력 기본 설정
        binding.calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(2017, 0, 1))
            .setMaximumDate(CalendarDay.from(2030, 11, 31))
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            binding.textview1.setText(date.year.toString() + "년 " + (date.month + 1).toString() + "월 " + date.day.toString() + "일의 기록")
            // 캘린더에서 선택한 날짜에 사용한 총 지출 값을 데이터베이스에서 받아오기
            // & 해단 날짜의 지출 내역 리싸이클러뷰에 달아주는 기능 함수
            getData(date.year, (date.month + 1), date.day)
            // 챌린지 성공시 팝업창 띄우기
            isCallengeSuccess(date.year, (date.month + 1), date)
            items.clear()
        }
    }


    // 함수 내에서 valueEventListener로 값을 받아올 경우, 리스너 안에서 전역 변수 or 함수 내의 지역 변수를 수정해도 반영되지 않음
    // 오직 리스너 안에서만 제대로된 값이 들어가 있음
    // -> OnCreate() 안에 데이터를 받아오는 리스너를 1번만 달고, 그 안에서 전역변수를 수정하면 제대로 값이 반영된다고 했음
    // -> but 그렇게 수정하기에는 작성 시간 촉박해서 함수 기능 추상화 제대로 못함ㅠㅠ
    private fun totalFun(total:Int){
        total_final = total
    }
    // 챌린지 예산 내로 소비한 경우, 캘린더에 표시 & 팝업 띄우기 가능
    private fun isCallengeSuccess(year_cal: Int, month_cal: Int, date: CalendarDay) {
        var year_cal_str = year_cal.toString()
        var month_cal_str = ""
        var str = ""
        if (month_cal < 10) {
            month_cal_str = "0" + month_cal.toString()
        } else {
            month_cal_str = month_cal.toString()
        }

        // 예산 날짜(키값), 예산 정보 불러오기
        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var dayNum = 0
                var budgetData = snapshot.children
                for (i in budgetData) {
                    var budget_date = i.key.toString()
                    budget_date = budget_date.replace("-", " ")
                    var new_budget_date = budget_date.split(" ")
                    var budget_year = new_budget_date[0]
                    var budget_month = new_budget_date[1]
                    var budget = i.child("ramount").getValue().toString().toInt()

                    if (year_cal_str.equals(budget_year) && month_cal_str.equals(budget_month)) {
                        if (budget_month.equals("02")){
                            dayNum = 28
                        } else if (budget_month.equals("04") || budget_month.equals("06") || budget_month.equals("09") || budget_month.equals("11")) {
                            dayNum = 30
                        } else {
                            dayNum = 31
                        }

                        // budget/날짜수 >= total -> isSucces = true
                        if (budget / dayNum >= total_final) {
                            binding.calendarView.addDecorator(EventDecorator(Color.parseColor("#1D872A"), Collections.singleton(date)))
                            var dialog = AlertDialog.Builder(mainActivity)
                            dialog.setIcon(R.drawable.ic_baseline_done_outline_24)
                            dialog.setTitle(date.year.toString() + "년 " + (date.month + 1).toString() + "월 " + date.day.toString() + "일의 기록")
                            dialog.setView(layoutInflater.inflate(R.layout.dialog_succes, null))
                            dialog.setMessage("오늘의 챌린지 성공!")
                            dialog.show()
                        } else {
                            binding.calendarView.addDecorator(EventDecorator(Color.RED, Collections.singleton(date)))
                            var dialog = AlertDialog.Builder(mainActivity)
                            dialog.setIcon(R.drawable.ic_baseline_done_outline_24)
                            dialog.setTitle(date.year.toString() + "년 " + (date.month + 1).toString() + "월 " + date.day.toString() + "일의 기록")
                            dialog.setView(layoutInflater.inflate(R.layout.dialog_fail, null))
                            dialog.setMessage("오늘의 챌린지 실패 ㅠ..ㅠ")
                            dialog.show()
                        }
                    } else {
                        println("해당월의 예산이 아님")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 읽어오기 실패했을 경우
                Toast.makeText(mainActivity, "데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getData(year_cal:Int, month_cal:Int, day_cal:Int) {
        var year_cal_str = year_cal.toString()
        var month_cal_str = ""
        var day_cal_str = ""
        var str = ""
        // 해당 날짜의 총 지출
        var total :Int = 0
        if (month_cal < 10) {
            month_cal_str = "0" + month_cal.toString()
        } else {
            month_cal_str = month_cal.toString()
        }
        if (day_cal < 10) {
            day_cal_str = "0" + day_cal.toString()
        } else {
            day_cal_str = day_cal.toString()
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val expense = snapshot.child("expenses")
                for(data in expense.children) {
                    var pay = data.child("ramount").getValue()
                    var names = data.child("rcategory").getValue()
                    var date = data.child("rdate").getValue().toString()
                    var memo = data.child("rmemo").getValue()
                    date = date.replace("-", " ")
                    var newdate = date.split(" ")
                    var year = newdate[0]
                    var month = newdate[1]
                    var day = newdate[2]

                    if (year_cal_str.equals(year) && month_cal_str.equals(month) && day_cal_str.equals(day)) {
                        total = total + pay.toString().toInt()
                        str = "\t\t\t\t\t\t\t" + names.toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + memo.toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + pay.toString()
                        items.add(str)
                    }
                }

                initRecyclerview(items)
                totalFun(total)
            }

            override fun onCancelled(error: DatabaseError) {
                // 읽어오기 실패했을 경우
                Toast.makeText(mainActivity, "데이터 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun initRecyclerview(items : ArrayList<String>) {
        binding.recyclerView2.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.VERTICAL, false)
        adapter2 = MyCalAdapter(items)
        binding.recyclerView2.adapter = adapter2
    }
}
