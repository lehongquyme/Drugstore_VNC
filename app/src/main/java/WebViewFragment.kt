@file:Suppress("DEPRECATION")

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drugstore_vnc.R
import com.example.drugstore_vnc.local.SharedPreferencesToken
import com.example.drugstore_vnc.util.CheckToPay
import com.example.drugstore_vnc.viewModel.ViewModelProductAPI
import com.example.drugstore_vnc.viewModelFatory.ViewModelFactory
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar
import okhttp3.Headers

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var back: ImageView
    private lateinit var googleProgressBar: GoogleProgressBar
    private lateinit var viewModel: ViewModelProductAPI
    private lateinit var productIdCart:MutableList<Int>
    private lateinit var  authToken :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authToken=SharedPreferencesToken(requireContext()).getToken()?.token.toString()
        viewModel = ViewModelProvider(
            this@WebViewFragment, ViewModelFactory(requireContext())
        )[ViewModelProductAPI::class.java]
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CheckToPay.binding?.bottomNavigationView?.visibility  =View.GONE
        val url = arguments?.getString("URL").toString()
        val listPay = arguments?.getIntegerArrayList("ListPay")
         productIdCart = listPay?.toMutableList() ?: mutableListOf()
        val rootView = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = rootView.findViewById(R.id.webView)
        googleProgressBar = rootView.findViewById(R.id.googleProgressBar)
        back = rootView.findViewById(R.id.backWebFragment)

        back.setOnClickListener {
            requireFragmentManager().popBackStack()

        }
        if (url.isNotEmpty()&& url!=""&& url!="null") {

            if (productIdCart.isNotEmpty()) {
                productIdCart.forEach { i ->
                    viewModel.updateQuantityInCart(i, 0)
                }
            }
        }
        googleProgressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            googleProgressBar.visibility = View.GONE
            loadWebView(url)

        }, 1000)

        return rootView
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(url: String) {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webView.loadUrl(url, createHeaders(authToken))
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                googleProgressBar.progress = newProgress
            }
        }


        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                googleProgressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                googleProgressBar.visibility = View.GONE
            }
        }


    }
    private fun createHeaders(jwt: String): Map<String, String> {
        val headers = Headers.Builder()
            .add("Authorization", "Bearer $jwt")
            .build()

        return headers.toMultimap().mapValues { it.value.first() }
    }

}
