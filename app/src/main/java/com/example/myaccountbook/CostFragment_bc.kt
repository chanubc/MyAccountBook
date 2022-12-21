package com.example.myaccountbook

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myaccountbook.databinding.FragmentCostBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//기록부분의 main fragmenet
class CostFragment_bc : Fragment() {

    //destroty에서 null로 초기화해야 하기 때문에 이런식으로 binding 달아야함함
   private var _binding: FragmentCostBinding? = null
    private val binding get() = _binding!!

    // 상속 받아야 할 것들

    lateinit var Piechart: PieChart
    lateinit var adapter: MyAdapter
    lateinit var rdb: DatabaseReference //데이터 베이스

    // <원형 그래프 관련 부분>
    val colorArray = ArrayList<Int>() //원형그래프 색깔 저장
    var pieArray = ArrayList<PieEntry>() //원형그래프 데이터 저장

    // 날짜,시간
    var currentYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
    var currentMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
    //붙일때 var val 조심할것!

    var isPause: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCostBinding.inflate(inflater, container, false)

//        Piechart = binding.pieChart
//        Piechart.setUsePercentValues(false) /*아래에 퍼센티지로 출력*/
        // Inflate the layout for this fragment
        return binding.root
    }

    //view의 초기화 하는 곳
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            Piechart = pieChart
            Piechart.setUsePercentValues(false) /*아래에 퍼센티지로 출력*/
            getdata()
            //inlayot
            //initrecylcer
            colorLayout()
        }
    }

    override fun onDestroyView() {
        // fragment 가 깨진 경우 바인딩은 null 로
        _binding = null
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        pieArray.clear()
    }

    override fun onResume() {
        super.onResume()
        //첫 실행 시 중복 생성 회피용
        if(isPause){
            getdata()
            isPause = false
        }
    }

    private fun getdata() {
        rdb = Firebase.database.getReference("Record")
        val query = rdb.child("expenses").limitToLast(50)

        var getdata = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ab = StringBuilder()
//                var printArray = ""
                var catArray = ArrayList<MyData>()

                for (i in snapshot.getChildren()) {
                    //firebase에 직접 접근
//                    var path = i.childrenCount
                    var pay = i.child("ramount").getValue()
                    var names = i.child("rcategory").getValue()
                    var date = i.child("rdate").getValue().toString()
                    date = date.replace("-", " ")
                    var newdate = date.split(" ")
                    var year = newdate[0]
                    var month = newdate[1]

                    //이번 년, 이번 월의 기록 확인
                    if (year != currentYear || month != currentMonth) {
                        continue
                    }
                    //같은 카테고리(커피==커피) 총액 더하기
                    var flag: Boolean = true
                    for (a in catArray) {
                        if (a == null) {
                            break
                        }
                        if (a.key == names.toString()) {
                            a.value += pay.toString().toFloat()
                            flag = false
                            break
                        }
                    }
                    if (flag) {
                        catArray.add(MyData(pay.toString().toFloat(), names.toString()))
                    }

                }
                //데이터 삽입
                for (i in catArray) {
                    pieArray.add(PieEntry(i.value, i.key))
                }

                //원형그래프 데이터 삽입
                initLayout(pieArray)

                //recyclerview 달기
                initRecyclerView()


            }

            override fun onCancelled(error: DatabaseError) {
            }


        }
        rdb.child("expenses").addListenerForSingleValueEvent(getdata)
    }

    private fun initRecyclerView() {

        binding.textView4.text = "${currentMonth}월의 지출"
        //기존 adapter(recyclerview adpater)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.VERTICAL, false)
        adapter = MyAdapter(pieArray)
        binding.recyclerView.adapter = adapter


        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                adapter.moveItem(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.bindingAdapterPosition)
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun initLayout(pieArray: ArrayList<PieEntry>) {

        val pieDataSet = PieDataSet(pieArray, "")
        pieDataSet.apply {
            colors = colorArray

            valueTextColor = Color.parseColor("#353C49")
            valueTextSize = 16f
        }


        val pieData = PieData(pieDataSet)
        Piechart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = true
            centerText = "지출"
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            transparentCircleRadius = 61f
            setEntryLabelColor(Color.parseColor("#6D7582"))
            animateY(1000, Easing.EasingOption.EaseInOutCubic)
            animate()
        }

    }

    private fun colorLayout() {

        /*for (c in ColorTemplate.VORDIPLOM_COLORS) colorArray.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorArray.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colorArray.add(c)
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorArray.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorArray.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorArray.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorArray.add(c)*/

        /* for (c in ColorTemplate.VORDIPLOM_COLORS) colorArray.add(c)
         for (c in ColorTemplate.JOYFUL_COLORS) colorArray.add(c)

         colorArray.add(ColorTemplate.getHoloBlue())*/


        //색 직접 입력 가능
//        Color.parseColor.text.setTextColor(Color.parseColor("#FFFFFF"))


        /*colorArray.add(Color.parseColor("#FFAC30"))
        colorArray.add(Color.parseColor("#BEAFFE"))
        colorArray.add(Color.parseColor("#AFECFE"))
        colorArray.add(Color.parseColor("#FFD6AD"))*/

        colorArray.add(Color.parseColor("#8CC0DE"))
        colorArray.add(Color.parseColor("#FAF0D7"))
        colorArray.add(Color.parseColor("#AFECFE"))
        colorArray.add(Color.parseColor("#F4BFBF"))
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorArray.add(c)
        colorArray.add(ColorTemplate.getHoloBlue())

    }


}


