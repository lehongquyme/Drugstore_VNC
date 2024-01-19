package com.example.drugstore_vnc.fragment.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.databinding.FragmentManageBinding
import com.example.drugstore_vnc.util.CheckToPay

class ManageFragment : Fragment() {
    private lateinit var binding: FragmentManageBinding
    private var link = "http://18.138.176.213/system/general_information"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.VISIBLE
        binding = FragmentManageBinding.inflate(inflater, container, false)
        binding.constraintLayoutInfor.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.infoManagerFragment);
        }
        binding.constraintLayoutSupport.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.contactFragment);
        }
        binding.constraintLayoutNews.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.newsFragment);
        }

        binding.constraintLayoutAbout.setOnClickListener {
            
                val bundle = bundleOf("URL" to "$link/1")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayOuttermsOfUse.setOnClickListener {
            
                val bundle = bundleOf("URL" to "$link/2")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutPrivacyPolicy.setOnClickListener {
            
                val bundle = bundleOf("URL" to "$link/3")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutShoppingGuide.setOnClickListener {
            
                val bundle = bundleOf("URL" to "$link/4")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutPaymentPolicy.setOnClickListener {
            

                val bundle = bundleOf("URL" to "$link/5")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutDeliveryPolicy.setOnClickListener {
            

                val bundle = bundleOf("URL" to "$link/6")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutReturnPolicy.setOnClickListener {
            

                val bundle = bundleOf("URL" to "$link/7")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutManagerWarrantyPolicy.setOnClickListener {
            

                val bundle = bundleOf("URL" to "$link/8")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutInspectionPolicy.setOnClickListener {
            

                val bundle = bundleOf("URL" to "$link/9")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }
        binding.constraintLayoutDuty.setOnClickListener {
            
                val bundle = bundleOf("URL" to "$link/10")
                view?.findNavController()?.navigate(R.id.webViewFragment, bundle)
           
        }


        return binding.root

    }


}