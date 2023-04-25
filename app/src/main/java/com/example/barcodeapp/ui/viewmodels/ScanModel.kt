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

    private val _productsList = MutableLiveData<MutableList<Element>?>(mutableListOf())
    private val _productRetrieved = MutableLiveData<Element?>()
    private val _priceSum =
        MutableLiveData(_productsList.value!!.sumOf { element: Element -> element.product.price.toDouble() })

    val productsList: LiveData<MutableList<Element>?>
        get() = _productsList
    val priceSum: LiveData<Double>
        get() = _priceSum
    val productRetrieved: LiveData<Element?>
        get() = _productRetrieved

    fun getProductByCodeBar(barCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val product = productsRepository.getProductById(barCode)
            if (product != null) {
                val cartElement = Element(product, buyQuantity = 1)
                _productRetrieved.postValue(cartElement)
            } else {
                _productRetrieved.postValue(null)
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            productsRepository.updateProduct(product)
        }
    }

    fun addProductToCart(product: Product): Boolean {
        Log.i(TAG, "adding ProductToCart: $product")
        val list = _productsList.value
        val found = list?.find { element -> element.product == product } != null
        if (found) return false
        val added = list?.add(Element(product)) ?: false
        _productsList.postValue(list)
        updateSumPrice()
        return added
    }

    fun deleteProduct(product: Product): Boolean {
        Log.i(TAG, "deleteProduct: $product")
        val list = _productsList.value
        val removed = list?.removeIf { element ->
            element.product.codeBar == product.codeBar
        } ?: false
        _productsList.postValue(list)
        updateSumPrice()
        return removed
    }

    private fun updateSumPrice() {
        _priceSum.postValue(_productsList.value!!.sumOf { element: Element ->
            element.product.price.toDouble() * element.buyQuantity
        })
    }

    fun addTo(product: Product) {
        val list = _productsList.value
        if (!list.isNullOrEmpty()) {
            val indexOf =
                list.indexOfFirst { element -> element.product.codeBar == product.codeBar }
            if (indexOf != -1) {
                if (list[indexOf].buyQuantity >= list[indexOf].product.quantity) return
                list[indexOf].buyQuantity++
                _productsList.postValue(list)
            }
        }
        updateSumPrice()
    }

    fun minusFrom(product: Product) {
        val list = _productsList.value
        if (!list.isNullOrEmpty()) {
            val indexOf =
                list.indexOfFirst { element -> element.product.codeBar == product.codeBar }
            if (indexOf != -1) {
                if (product.quantity <= 1) return
                list[indexOf].buyQuantity--
                _productsList.postValue(list)
            }
        }
        updateSumPrice()
    }

    fun scanForProduct(context: Context, onScanProduct: OnScanProduct) {
        scanProduct(context, onScanProduct)
    }
}

data class Element(var product: Product, var buyQuantity: Int = 1)