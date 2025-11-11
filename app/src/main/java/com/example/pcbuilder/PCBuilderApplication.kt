package com.example.pcbuilder
import android.app.Application
import com.example.pcbuilder.data.AppDatabase

class PCBuilderApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}