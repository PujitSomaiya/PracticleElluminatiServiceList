package com.pricelistingforservice.view.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pricelistingforservice.databinding.ItemSingleSelectionBinding
import com.pricelistingforservice.util.Utils
import com.pricelistingforservice.view.home.model.ListItem
import java.util.*

class SingleSelectionAdapter(
    var listItem: ArrayList<ListItem>,
    val mContext: Context,
    private val itemClick: (result: ListItem) -> Unit
) :
    RecyclerView.Adapter<SingleSelectionAdapter.DataViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            ItemSingleSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(position)

        holder.itemView.setOnClickListener {
            if (!listItem[position].isDefaultSelected) {
                for (i in listItem.indices) {
                    listItem[i].isDefaultSelected = false
                }
                itemClick.invoke(listItem[position])
                notifyDataSetChanged()
            }
        }
    }

    inner class DataViewHolder constructor(val binding: ItemSingleSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val value = listItem[position]
            binding.tvTitle.text = value.name!![0].toString()
            binding.tvPrice.text = "${Utils.getCurrencySymbol("INR")} ${value.price.toString()}"
            binding.rbSelection.isClickable = false
            binding.rbSelection.isChecked = value.isDefaultSelected
            binding.executePendingBindings()
        }
    }

}