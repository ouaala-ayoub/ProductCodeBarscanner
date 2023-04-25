package com.example.barcodeapp.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductPriceInputModel(initialPrice: String, initialQuantity: String) : ViewModel() {
    private val price = MutableLiveData(initialPrice)
    private val quantity = MutableLiveData(initialQuantity)

    val isValid = MediatorLiveData<Boolean>().apply {
        addSource(price) { price ->
            this.value = validateData(price, quantity.value)
        }
        addSource(quantity) { quantity ->
            this.value = validateData(price.value, quantity)
        }
    }

    fun setThePrice(priceNew: String) {
        price.postValue(priceNew)
    }

    fun setTheQuantity(newQuantity: String) {
        quantity.postValue(newQuantity)
    }

    private fun validateData(price: String?, quantity: String?) =
        !price.isNullOrBlank() && !quantity.isNullOrBlank()
}