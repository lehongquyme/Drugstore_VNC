package com.example.drugstore_vnc.fragment.manager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.fragment.history.model.history.DataType

class ItemRecyclerAdapter (
):
    RecyclerView.Adapter<ItemRecyclerAdapter.MyViewHolder>() {
        private lateinit var list: List<DataType>
    fun setList(item: List<DataType>){
        item.let {
            list=it
        }

    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipe_layout)
        val mainContent: TextView = itemView.findViewById(R.id.main_content)
        val btnSwipeEdit: LinearLayout = itemView.findViewById(R.id.btnSwipeEdit)
        val btnSwipeDelete: LinearLayout = itemView.findViewById(R.id.btnSwipeDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_swipe_button, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
        holder.mainContent.text= item.name
        holder.swipeLayout.addSwipeListener(object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout) {
                // Handle swipe open event if needed
            }

            override fun onClose(layout: SwipeLayout) {
                // Handle swipe close event if needed
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}