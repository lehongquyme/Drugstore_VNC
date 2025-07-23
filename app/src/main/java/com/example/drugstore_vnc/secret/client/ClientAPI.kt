package com.example.drugstore_vnc.client

import android.annotation.SuppressLint
import android.content.Context
import com.example.drugstore_vnc.local.SharedPreferencesToken
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("StaticFieldLeak")
object ClientAPI {

    private var retrofit: Retrofit? = null
    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("http://18.138.176.213/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }


    fun getClientProduct( context: Context): Retrofit {
        val token = SharedPreferencesToken(context).getToken()?.token
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header(
                        "Authorization",
                        "Bearer $token"
                    ) // Add the token to the Authorization header
                val request: Request = requestBuilder.build()

                chain.proceed(request)
            }
        }.build()
        retrofit = Retrofit.Builder()
            .baseUrl("http://18.138.176.213/api/") // Replace with your actual base URL
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        return retrofit!!
    }

}