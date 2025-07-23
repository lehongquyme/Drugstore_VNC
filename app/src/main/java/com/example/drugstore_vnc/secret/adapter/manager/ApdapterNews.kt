package com.example.drugstore_vnc.adapter.manager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.fragment.manager.model.news.DataNews
import com.squareup.picasso.Picasso

class ApdapterNews(private val items: List<DataNews>,
): RecyclerView.Adapter<ApdapterNews.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemnewsgeneral, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.itemDateNewsGeneral.text = item.ngayCongKhai
        holder.itemTittleNewsGeneral.text = item.tieuDe
        holder.itemContentNewsGeneral.text = item.moTa
        Picasso.get().load(item.img).into(holder.itemImageNewsGeneral)

        holder.linearLayoutNews.setOnClickListener {
                val bundle = bundleOf("URL" to item.url)
            holder.linearLayoutNews.findNavController().navigate(R.id.webViewFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return items.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemDateNewsGeneral: TextView = itemView.findViewById(R.id.dateNewsGeneral)
        val itemTittleNewsGeneral: TextView = itemView.findViewById(R.id.titleNewsGeneral)
        val itemContentNewsGeneral: TextView = itemView.findViewById(R.id.contentNewsGeneral)
        val itemImageNewsGeneral: ImageView = itemView.findViewById(R.id.imageNewsGeneral)
        val linearLayoutNews: LinearLayout = itemView.findViewById(R.id.linearLayoutNews)

    }
}

