package com.example.myaccountbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SpendViewModel : ViewModel() {
    lateinit var rdb:DatabaseReference

    var categoryList = ArrayList<MyCategory>().apply {
        for (item in SpendCategoryEnum.values()){
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
    private val _spendCategoryList = MutableLiveData<ArrayList<MyCategory>>().apply {
        value = ArrayList(defaultCategoryList)
    }
    val spendCategoryList: LiveData<ArrayList<MyCategory>> get() = _spendCategoryList

    private val _notifySelectedCategoryItem =
        MutableLiveData<Pair<Pair<MyCategory, Int>, Pair<MyCategory, Int>>>()
    val notifySelectedCategoryItem: LiveData<Pair<Pair<MyCategory, Int>, Pair<MyCategory, Int>>>
        get() = _notifySelectedCategoryItem

    private val _spendAmount = MutableLiveData<String>()
    val spendAmount: LiveData<String> get() = _spendAmount

    val spendExplain = MutableLiveData<String>()

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _selectedTime = MutableLiveData<String>()
    val selectedTime: LiveData<String> get() = _selectedTime

    private val _isActivated = MutableLiveData(false)
    val isActivated: LiveData<Boolean> get() = _isActivated

    val selectDateEvent = SingleLiveEvent<Unit>()
    val selectTimeEvent = SingleLiveEvent<Unit>()

    private var selectedCategory: String ?= null
    private var latestClicked = 0

    val recordSpendFinishEvent = SingleLiveEvent<String>()

    fun editModeInit(title: String, category: String){
        spendExplain.value = title
        selectedCategory = SpendCategoryEnum.getEnumByTitle(category).title
        val spendCategoryEnumList = SpendCategoryEnum.values()

        for(i in spendCategoryEnumList.indices) {
            if (category == spendCategoryEnumList[i].title) {
                _notifySelectedCategoryItem.value = Pair(
                    Pair(selectedCategoryList[i], i),
                    Pair(selectedCategoryList[i], i)
                )
                _spendCategoryList.value?.set(i, selectedCategoryList[i])
                latestClicked = i
                break
            }
        }
        checkSubmit()
    }

    fun categoryItemClick(spendCategory: MyCategory) {
        selectedCategory = spendCategory.title

        val values = SpendCategoryEnum.values()
        for (i in values.indices){
            if (spendCategory.title == values[i].title){
                _notifySelectedCategoryItem.value = Pair(
                    Pair(defaultCategoryList[latestClicked], latestClicked),
                    Pair(selectedCategoryList[i], i)
                )
                _spendCategoryList.value?.set(i, selectedCategoryList[i])
                _spendCategoryList.value?.set(
                    latestClicked,
                    defaultCategoryList[latestClicked]
                )
                latestClicked = i
                break
            }
        }
        checkSubmit()
    }

    fun setSpendAmount(value:String) {
        _spendAmount.value = value
    }

    fun selectDate() {
        selectDateEvent.call()
    }

    fun selectTime() {
        selectTimeEvent.call()
    }

    fun setDate(value: String) {
        _selectedDate.value = value
    }

    fun setTime(value: String) {
        _selectedTime.value = value
    }


    fun checkSubmit() {
        val amount = spendAmount.value
        val explain = spendExplain.value

        _isActivated.value =
            !amount.isNullOrEmpty() && !explain.isNullOrEmpty() && !selectedCategory.isNullOrEmpty()
    }

    fun recordSpend() {
        rdb = Firebase.database.getReference("Record")
        val nDate = Date(System.currentTimeMillis())
        var fDate = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(nDate)
        val item = ExRecord(
            fDate,
            selectedCategory ?: return,
            spendAmount.value?.replace(",", "")?.toInt() ?: return,
            spendExplain.value ?: return
        )
        rdb.child("expenses").child(fDate).setValue(item)
    }

    fun clearInput() {
    }
}