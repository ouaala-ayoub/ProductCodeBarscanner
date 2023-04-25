package com.example.barcodeapp.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.repositories.ProductsRepository
import com.example.barcodeapp.utils.OnScanProduct
import com.example.barcodeapp.utils.scanProduct
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ProductsModel"

class ProductsModel(private val productsRepository: ProductsRepository) : ViewModel() {

    val productsList = productsRepository.getAllProducts()

    fun addProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.deleteProduct(product)
        }
    }

    fun scanProductAndResult(context: Context, onScanProduct: OnScanProduct) {
        scanProduct(context, onScanProduct)
    }

    fun getProductById(productId: String) = productsRepository.getProductById(productId)
}