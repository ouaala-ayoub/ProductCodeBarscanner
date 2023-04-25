package com.example.barcodeapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProductPriceInputModel(initialPrice: String) : ViewModel() {
    private val _price = MutableLiveData(initialPrice)
    private val price: LiveData<String>
        get() = _price

    val isValid = MediatorLiveData<Boolean>().apply {
        addSource(price) { price ->
            this.value = validateData(price)
        }
    }

    fun setThePrice(price: String) {
        _price.postValue(price)
    }

    private fun validateData(price: String) = price.isNotBlank()
}