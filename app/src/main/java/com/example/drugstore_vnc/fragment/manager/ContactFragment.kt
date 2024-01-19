package com.example.drugstore_vnc.fragment.manager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drugstore_vnc.databinding.FragmentContactBinding
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelSpinnerAPI
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactFragment : Fragment() {

    private lateinit var contactViewModel: ViewModelSpinnerAPI
    private lateinit var binding: FragmentContactBinding
    private var phone = ""
    private var linkFaceBook = ""
    private var linkZalo = ""
    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactViewModel =
            ViewModelProvider(this@ContactFragment)[ViewModelSpinnerAPI::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        contactViewModel.contact.observe(viewLifecycleOwner) { data ->
            job = CoroutineScope(Dispatchers.Main).launch {
                delay(2000) // Simulate a 2-second delay
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.linearLayoutContact.visibility= View.GONE
                binding.linearLayoutContact1.visibility= View.VISIBLE
            }

            Picasso.get().load(data[0].icon).into(binding.imageFaceBook1)
            Picasso.get().load(data[1].icon).into(binding.imageZalo1)
            Picasso.get().load(data[2].icon).into(binding.imagePhone1)
            binding.textFaceBook1.text = data[0].name
            binding.textZalo1.text = data[1].name
            binding.textPhone1.text = data[2].name
            linkFaceBook = data[0].value
            linkZalo = data[1].value
            extractPhoneNumber(data[2].value)?.let { phone = it }

        }
        binding.backManagerFragment.setOnClickListener {
            requireFragmentManager().popBackStack()
        }
        binding.constraintFaceBook1.setOnClickListener {
            startActivity(getOpenFacebookIntent())

        }
        binding.constraintZalo1.setOnClickListener {
            startActivity(getOpenZaloIntent())
        }
        binding.constraintPhone1.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + phone)
            startActivity(dialIntent)
        }

        contactViewModel.fetchContact()
        return binding.root

    }

    private fun getOpenFacebookIntent(): Intent {
        return try {
            requireActivity().packageManager.getPackageInfo("com.facebook.katana", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse(linkFaceBook))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/appetizerandroid"))
        }
    }

    private fun getOpenZaloIntent(): Intent {
        return try {
            requireActivity().packageManager.getPackageInfo("com.zing.zalo", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse(linkZalo))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://zalo.me/YourZaloPageID"))
        }
    }

    fun extractPhoneNumber(input: String): String? {
        val regex = Regex("""\b\d{10,11}\b""")
        val matchResult = regex.find(input)
        return matchResult?.value
    }
}