package com.example.busschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.busschedule.SiteAdapter.SiteViewHolder
import com.example.busschedule.database.Site
import com.example.busschedule.databinding.SiteItemBinding

class SiteAdapter(private val onItemClicked: (Site) -> Unit): ListAdapter<Site, SiteViewHolder>(DiffCallback) {

    class SiteViewHolder(private var binding: SiteItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(site: Site) {

                binding.siteIdTextView.text = site.siteId.toString()
                binding.siteNameTextView.text = site.siteName
                binding.siteArrondissementTextView.text = site.arrondissement.toString()
                binding.siteUrlTextView.text = site.url
                binding.siteImgFileTextView.text = site.imgFile
                binding.siteNotesTextView.text = site.notes
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Site>() {
            override fun areItemsTheSame(oldItem: Site, newItem: Site): Boolean {
                return oldItem.siteId == newItem.siteId
            }

            override fun areContentsTheSame(oldItem: Site, newItem: Site): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val viewHolder = SiteViewHolder(
            SiteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}