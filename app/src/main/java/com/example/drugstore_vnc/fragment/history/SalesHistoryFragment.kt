package com.example.drugstore_vnc.fragment.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.drugstore_vnc.R


class SalesHistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView= inflater.inflate(R.layout.fragment_sales_history, container, false)
        val image = rootView.findViewById<ImageView>(R.id.imageSalesHistory)

        // Load the GIF into the ImageView using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.fail_history) // Replace "your_gif_resource" with the actual resource ID of your GIF
            .into(image)
        return rootView
    }


}