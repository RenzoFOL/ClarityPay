package com.example.claritypay

import android.app.Application
import com.example.claritypay.di.AppContainer
import com.example.claritypay.di.AppDataContainer

class ClarityPayApp : Application() {
    val container: AppContainer by lazy { AppDataContainer(this) }
}
