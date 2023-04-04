package com.weather.demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weather.demo.databinding.RowDropDownBinding

class DropDownAdapter(private var list: MutableList<String>, var listner: (String, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowDropDownBinding.inflate(inflater, parent, false)
        return View1ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as View1ViewHolder).bindView(list[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = list.size

    inner class View1ViewHolder(var binding: RowDropDownBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: String) {
            binding.txtDropDown.text = item

            binding.txtDropDown.setOnClickListener {
                listner.invoke(item, adapterPosition)
            }
        }
    }
}