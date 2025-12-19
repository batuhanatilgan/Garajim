package com.batuhanatilgan.garajim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.batuhanatilgan.garajim.databinding.ItemFuelBinding

class FuelAdapter(private var fuelList: List<Fuel>) :
    RecyclerView.Adapter<FuelAdapter.FuelViewHolder>() {

    class FuelViewHolder(val binding: ItemFuelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelViewHolder {
        val binding = ItemFuelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FuelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FuelViewHolder, position: Int) {
        val item = fuelList[position]

        holder.binding.txtFuelDate.text = item.tarih
        // Örn: 42.0 Lt x 40.0 TL
        holder.binding.txtFuelInfo.text = "${item.alinanLitre} Lt x ${item.litreFiyati} TL"
        holder.binding.txtFuelCost.text = "${item.toplamTutar} TL"
        holder.binding.txtFuelKm.text = "${item.oAnkiKm} KM"
    }

    override fun getItemCount(): Int {
        return fuelList.size
    }

    fun updateList(newList: List<Fuel>) {
        fuelList = newList
        notifyDataSetChanged()
    }
}