package com.example.barcodeapp.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.repositories.ProductsRepository
import com.example.barcodeapp.utils.OnScanProduct
import com.example.barcodeapp.utils.scanProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ScanModel"

class ScanModel(private val productsRepository: ProductsRepository) : ViewModel() {

    private val _productsList = MutableLiveData<MutableList<Product>?>(mutableListOf())
    private val _productRetrieved = MutableLiveData<Product?>()
    private val _priceSum =
        MutableLiveData(_productsList.value!!.sumOf { product: Product -> product.price.toDouble() })

    val productsList: LiveData<MutableList<Product>?>
        get() = _productsList
    val priceSum: LiveData<Double>
        get() = _priceSum
    val productRetrieved: LiveData<Product?>
        get() = _productRetrieved

    fun getProductByCodeBar(barCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val product = productsRepository.getProductById(barCode)
            _productRetrieved.postValue(product)
        }
    }

    fun addProductToCart(product: Product): Boolean {
        Log.i(TAG, "adding ProductToCart: $product")
        val list = _productsList.value
        val added = list?.add(product) ?: false
        _productsList.postValue(list)
        updateSumPrice()
        return added
    }

    fun deleteProduct(product: Product): Boolean {
        Log.i(TAG, "deleteProduct: $product")
        val list = _productsList.value
        val removed = list?.remove(product) ?: false
        _productsList.postValue(list)
        updateSumPrice()
        return removed
    }

    private fun updateSumPrice() {
        _priceSum.postValue(_productsList.value!!.sumOf { product: Product -> product.price.toDouble() })
    }

    fun scanForProduct(context: Context, onScanProduct: OnScanProduct) {
        scanProduct(context, onScanProduct)
    }
}