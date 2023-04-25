package com.example.barcodeapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.barcodeapp.data.models.Product

@Dao
interface ProductsDao {
    @Query("SELECT * From Product")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM Product WHERE code_bar == :productId")
    fun getProductById(productId: String): Product?

    @Upsert
    fun insertProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Delete
    fun deleteProduct(product: Product)
}