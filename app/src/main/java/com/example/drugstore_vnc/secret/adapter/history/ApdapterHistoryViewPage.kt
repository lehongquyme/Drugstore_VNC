package com.example.drugstore_vnc.adapter.history

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.drugstore_vnc.fragment.history.PurchaseHistoryFragment
import com.example.drugstore_vnc.fragment.history.SalesHistoryFragment

class ApdapterHistoryViewPage(fm: Fragment) : FragmentStateAdapter(fm) {
    companion object {
        private const val NUM_TABS = 2
    }

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> SalesHistoryFragment()
            else -> PurchaseHistoryFragment()
        }
    }
}