package com.batuhanatilgan.garajim

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.batuhanatilgan.garajim.databinding.ItemMaintenanceBinding

class MaintenanceAdapter(private var maintenanceList: List<Maintenance>) :
    RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    class MaintenanceViewHolder(val binding: ItemMaintenanceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val binding = ItemMaintenanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MaintenanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val item = maintenanceList[position]
        holder.binding.txtDate.text = item.tarih
        holder.binding.txtKm.text = "${item.kilometre} KM"
        holder.binding.txtOperations.text = item.yapilanIslemler
        holder.binding.txtNotes.text = item.notlar
        holder.binding.txtCost.text = "${item.maliyet} TL"
    }

    override fun getItemCount(): Int {
        return maintenanceList.size
    }
    fun updateList(newList: List<Maintenance>) {
        maintenanceList = newList
        notifyDataSetChanged()
    }
}