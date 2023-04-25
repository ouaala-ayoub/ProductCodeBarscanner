package com.example.barcodeapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodeapp.R
import com.example.barcodeapp.data.db.ProductsDataBase
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.models.ShowMethodEnum
import com.example.barcodeapp.data.repositories.ProductsRepository
import com.example.barcodeapp.databinding.ActivityScanBinding
import com.example.barcodeapp.ui.adapters.ProductsAdapter
import com.example.barcodeapp.ui.viewmodels.ScanModel
import com.example.barcodeapp.utils.OnScanProduct
import com.example.barcodeapp.utils.toast
import com.google.mlkit.vision.barcode.common.Barcode

private const val TAG = "ScanActivity"

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var viewModel: ScanModel
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        viewModel = ScanModel(ProductsRepository(ProductsDataBase.getInstance(this).productsDao()))
        productsAdapter = ProductsAdapter(object : ProductsAdapter.OnProductClicked {
            override fun onDeleteClicked(product: Product) {
                viewModel.deleteProduct(product)
            }

            override fun onUpdateClicked(product: Product) {
                Log.i(TAG, "do nothing")
            }

            override fun onPlusClicked(product: Product): Nothing? {
                viewModel.addTo(product)
                return super.onPlusClicked(product)
            }

            override fun onMinusClicked(product: Product): Nothing? {
                viewModel.minusFrom(product)
                return super.onMinusClicked(product)
            }
        }, ShowMethodEnum.EDIT)


        binding.apply {
            productsScannedRv.apply {
                adapter = productsAdapter
                layoutManager = LinearLayoutManager(this@ScanActivity)
            }

            viewModel.apply {

                priceSum.observe(this@ScanActivity) { sum ->
                    cartSum.text = getString(R.string.total, sum.toString())
                }

                scanProducts.setOnClickListener {
                    val callBack = object : OnScanProduct {
                        override fun onSuccess(barcode: Barcode) {
                            val code = barcode.rawValue.toString()
                            getProductByCodeBar(code)
                            scanForProduct(this@ScanActivity, this)
                        }

                        override fun onFail(e: Exception) {
                            this@ScanActivity.toast(
                                e.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                        }

                        override fun onCanceled() {
                            Log.i(TAG, "Canceled scanning")
                        }
                    }
                    scanForProduct(this@ScanActivity, callBack)

                }



                productRetrieved.observe(this@ScanActivity) { product ->
                    if (product != null) {
                        addProductToCart(product.product)
                    } else {
                        //to add a dialog to add the product to the database
                        Log.e(TAG, "product is not inserted the database")
                    }
                }

                productsList.observe(this@ScanActivity) { elements ->
                    if (elements != null) {
                        productsAdapter.setProductsList(elements.map { element ->
                            Product(
                                element.product.codeBar,
                                element.product.price,
                                element.buyQuantity
                            )
                        })
                    } else {
                        Log.e(TAG, "products list is null")
                    }
                }
            }
        }

        setContentView(binding.root)
    }
}