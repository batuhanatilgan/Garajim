package com.batuhanatilgan.garajim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.batuhanatilgan.garajim.databinding.ItemTireBinding

class TireAdapter(private var tireList: List<TireChange>) :
    RecyclerView.Adapter<TireAdapter.TireViewHolder>() {

    class TireViewHolder(val binding: ItemTireBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TireViewHolder {
        val binding = ItemTireBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TireViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TireViewHolder, position: Int) {
        val item = tireList[position]

        holder.binding.txtTireType.text = "${item.takilanTur} Lastik Takıldı"
        holder.binding.txtTireDate.text = item.islemTarihi
        holder.binding.txtTireNotes.text = item.notlar

        // İkonu ve Rengi değiştir
        if (item.takilanTur == "Kışlık") {
            holder.binding.imgTireIcon.setImageResource(R.drawable.ic_snow)
        } else {
            holder.binding.imgTireIcon.setImageResource(R.drawable.ic_sun)
        }
    }

    override fun getItemCount(): Int {
        return tireList.size
    }

    fun updateList(newList: List<TireChange>) {
        tireList = newList
        notifyDataSetChanged()
    }
}