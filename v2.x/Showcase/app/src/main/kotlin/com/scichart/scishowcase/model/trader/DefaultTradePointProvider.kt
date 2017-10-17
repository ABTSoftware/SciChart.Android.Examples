//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// DefaultTradePointProvider.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.model.trader

import android.net.NetworkInfo
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class DefaultTradePointProvider(connectivityObservable: Observable<Connectivity>) : ITradePointsProvider {

    private interface IFinanceService {
        @GET("getprices")
        fun listTrades(@Query("q") stockSymbol: String, @Query("i") interval: Int, @Query("p") period: String, @Query("f") data: String = "d,o,h,l,c,v"): Call<ResponseBody>
    }

    private class FinanceConverterFactory : Converter.Factory() {
        override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *> {
            return Converter<ResponseBody, ResponseBody> { value -> value!! }
        }

        override fun stringConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, String> {
            return Converter<Any, String>(Any::toString)
        }
    }

    private class ConnectivityInterceptor(connectivityObservable: Observable<Connectivity>) : Interceptor {

        private var isNetworkActive = false

        init {
            connectivityObservable.doOnNext{ isNetworkActive = it.state == NetworkInfo.State.CONNECTED }.subscribe()
        }

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            if (!isNetworkActive) {
                throw IOException()
            } else {
                return chain.proceed(chain.request())
            }
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityInterceptor(connectivityObservable))
            .build()

    private val retrofit = Retrofit.Builder().baseUrl("https://finance.google.com/finance/")
            .addConverterFactory(FinanceConverterFactory())
            .client(okHttpClient)
            .build()

    private val service = retrofit.create(IFinanceService::class.java)

    override fun getTradePoints(tradeConfig: TradeConfig): TradeDataPoints {
        val data = TradeDataPoints()
        val response = service.listTrades(tradeConfig.symbol, tradeConfig.interval, tradeConfig.period).execute()
        if (response != null) {
            BufferedReader(response.body().charStream()).useLines {
                var time: Long = 0
                it.filter { it.contains(',') }.drop(1).forEach {
                    val split = it.split(',')
                    val timeString = split[0]
                    val open = split[1].toDouble()
                    val high = split[2].toDouble()
                    val low = split[3].toDouble()
                    val close = split[4].toDouble()
                    val volume = split[5].toDouble()

                    when {
                        timeString.startsWith("a") -> {
                            time = timeString.drop(1).toLong()
                        }
                        else -> {
                            time += 100 * 1000
                        }
                    }

                    data.append(time, open, high, low, close, volume)
                }
            }
        }
        return data
    }
}