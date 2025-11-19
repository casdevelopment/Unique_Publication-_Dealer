package com.publication.dealer.network.retofit

import android.content.Context
import android.util.Log
import com.publication.dealer.network.api.ApiInterface
import com.publication.dealer.util.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

    return Retrofit.Builder()
        .baseUrl(AppConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideOkHttpClient(context: Context): OkHttpClient {

    val interceptor = HttpLoggingInterceptor()
    return OkHttpClient.Builder()
        .addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()

            Log.v("AUTH_TOKEN","AUTH_TOKEN "+ AppConstants.AUTH_TOKEN)
            //request.addHeader("Authorization", AppConstants.Bearer + " " + AppConstants.AUTH_TOKEN)
            request.addHeader("Authorization",  AppConstants.AUTH_TOKEN)
            request.addHeader("Accept", "application/json")
            val response = chain.proceed(request.build())

            if (response.code==401){
                CoroutineScope(Dispatchers.Main).launch {
                    /*Toast.makeText(context, "Your session has expired!, Please Login again", Toast.LENGTH_LONG).show()
                    val i = Intent(context, SplashActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.putExtra("response",401)
                    context.startActivity(i)*/
                }
            }
            response

        }

        .connectTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .cache(null)
        .build()

}

fun provideRetrofitInterface(retrofit: Retrofit): ApiInterface = retrofit.create(ApiInterface::class.java)
