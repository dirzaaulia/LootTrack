package com.dirzaaulia.loottrack.di

import com.dirzaaulia.loottrack.data.PreferenceStorage
import com.dirzaaulia.loottrack.network.CheapSharkApi
import com.dirzaaulia.loottrack.network.CurrencyApi
import com.dirzaaulia.loottrack.network.createHttpEngine
import com.dirzaaulia.loottrack.viewmodel.DealsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single { createHttpEngine() }
    single { CheapSharkApi(get()) }
    single { CurrencyApi(get()) }
    single { PreferenceStorage() }
    viewModel { DealsViewModel(get(), get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
