package com.example.barcodeapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.barcodeapp.data.dao.ProductsDao
import com.example.barcodeapp.data.models.Product

private const val dbName = "products_db"

@Database(entities = [Product::class], version = 2)
abstract class ProductsDataBase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao

    companion object {
        private var productsDataBase: ProductsDataBase? = null
        fun getInstance(context: Context): ProductsDataBase {
            if (productsDataBase == null) {
                productsDataBase =
                    Room.databaseBuilder(context, ProductsDataBase::class.java, dbName)
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return productsDataBase!!
        }
    }
}