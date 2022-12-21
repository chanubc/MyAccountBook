package com.example.myaccountbook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myaccountbook.databinding.PierowBinding
import com.example.myaccountbook.databinding.RecyclerviewCalBinding
import com.github.mikephil.charting.data.PieEntry

class  MyCalAdapter(val items: ArrayList<String>) : RecyclerView.Adapter<MyCalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerviewCalBinding) : RecyclerView.ViewHolder(binding.root) {
        val expense_cal = binding.expenseCal

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewCalBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.expenseCal.text = items[position]

    }


    override fun getItemCount(): Int {
        return items.size
    }

}