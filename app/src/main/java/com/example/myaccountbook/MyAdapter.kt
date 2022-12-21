package com.example.myaccountbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myaccountbook.databinding.PierowBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieEntry

class  MyAdapter(val items: ArrayList<PieEntry>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun OnItemClick(data: PieEntry)
    }

    fun moveItem(oldPos: Int, newPos: Int) {
        val item = items[oldPos]
        items.removeAt(oldPos)
        items.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos: Int) {
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    var itemClickListener: OnItemClickListener? = null //초기값 null값

    inner class ViewHolder(val binding: PierowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.textName.setOnClickListener {
                itemClickListener?.OnItemClick(items[bindingAdapterPosition]) //?는 null일 수 도 있다고 알려주는 역할
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PierowBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        var isClicked: Boolean = true
        holder.binding.textName.text = items[position].label.toString()
        holder.binding.textCost.text = items[position].value.toInt().toString()+" won"
//        holder.binding.
//        holder.binding.textView2.visibility = View.GONE

        /*holder.binding.textView.setOnClickListener {
            if (isClicked) {
                holder.binding.textView2.visibility = View.VISIBLE
                isClicked = false
            } else {
                holder.binding.textView2.visibility = View.GONE
                isClicked = true
            }
        }*/
    }
    /*fun onRenewViewViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textName.text = items[position].label.toString()
        holder.binding.textCost.text = items[position].value.toInt().toString()
    }*/

    override fun getItemCount(): Int {
        return items.size
    }


}