package com.example.drugstore_vnc.fragment.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.adapter.history.ApdapterHistoryViewPage
import com.example.drugstore_vnc.util.CheckToPay
import com.google.android.material.tabs.TabLayout


class HistoryFragment : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_history, container, false)

        val tabLayout = rootView.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = rootView.findViewById<ViewPager2>(R.id.viewPager)
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.VISIBLE

        viewPager.adapter =  ApdapterHistoryViewPage(this)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager.currentItem = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}