package com.dirzaaulia.loottrack.network

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.dirzaaulia.loottrack.data.getAndroidContext
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpEngine(): HttpClientEngine = OkHttp.create {
    config {
        getAndroidContext()?.let { context ->
            addInterceptor(ChuckerInterceptor.Builder(context).build())
        }
    }
}
