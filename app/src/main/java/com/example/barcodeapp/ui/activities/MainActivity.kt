package com.example.barcodeapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.barcodeapp.data.db.ProductsDataBase
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.repositories.ProductsRepository
import com.example.barcodeapp.databinding.ActivityMainBinding
import com.example.barcodeapp.utils.goToActivity
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.apply {
            products.setOnClickListener {
                goToActivity<ProductsActivity>(this@MainActivity)
            }
            scan.setOnClickListener {
                goToActivity<ScanActivity>(this@MainActivity)
            }
        }

        setContentView(binding.root)
    }
}