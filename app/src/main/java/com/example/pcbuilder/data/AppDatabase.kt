package com.example.pcbuilder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pcbuilder.data.dao.ProductDao
import com.example.pcbuilder.data.dao.CartDao
import com.example.pcbuilder.data.model.CartItem
import com.example.pcbuilder.data.dao.UserDao
import com.example.pcbuilder.data.model.Product
import com.example.pcbuilder.data.model.User

@Database(entities = [User::class, Product::class, CartItem::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_app_database" 
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}