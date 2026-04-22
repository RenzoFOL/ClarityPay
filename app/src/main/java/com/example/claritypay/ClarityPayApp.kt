package com.example.claritypay

import android.app.Application
import com.example.claritypay.di.AppContainer

class ClarityPayApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
