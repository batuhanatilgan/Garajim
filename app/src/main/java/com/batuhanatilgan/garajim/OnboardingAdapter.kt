package com.batuhanatilgan.garajim

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class OnboardingItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

// Verileri sayfalara yerleştiren adaptör
class OnboardingAdapter(private val itemList: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgSlide = view.findViewById<ImageView>(R.id.imgSlide)
        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtDescription = view.findViewById<TextView>(R.id.txtDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = itemList[position]
        holder.imgSlide.setImageResource(item.imageRes)
        holder.txtTitle.text = item.title
        holder.txtDescription.text = item.description
    }

    override fun getItemCount(): Int = itemList.size
}