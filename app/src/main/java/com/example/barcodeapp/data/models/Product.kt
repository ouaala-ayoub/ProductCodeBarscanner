package com.example.barcodeapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(false) @ColumnInfo("code_bar") val codeBar: String,
    @ColumnInfo("price") var price: String = "0",
    @ColumnInfo("quantity") var quantity: Int = 1
)
