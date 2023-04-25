package com.example.barcodeapp.data.repositories

import com.example.barcodeapp.data.dao.ProductsDao
import com.example.barcodeapp.data.models.Product

class ProductsRepository(private val dao: ProductsDao) {
    fun getAllProducts() = dao.getAllProducts()
    fun getProductById(productId: String) = dao.getProductById(productId)
    fun insertProduct(product: Product) = dao.insertProduct(product)
    fun updateProduct(product: Product) = dao.updateProduct(product)
    fun deleteProduct(product: Product) = dao.deleteProduct(product)
}