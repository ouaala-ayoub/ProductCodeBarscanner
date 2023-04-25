package com.example.barcodeapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodeapp.R
import com.example.barcodeapp.data.db.ProductsDataBase
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.repositories.ProductsRepository
import com.example.barcodeapp.databinding.ActivityProductsBinding
import com.example.barcodeapp.ui.adapters.ProductsAdapter
import com.example.barcodeapp.ui.viewmodels.ProductsModel
import com.example.barcodeapp.utils.*
import com.google.mlkit.vision.barcode.common.Barcode

private const val TAG = "ProductsActivity"

class ProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsBinding
    private lateinit var viewModel: ProductsModel
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityProductsBinding.inflate(layoutInflater)
        viewModel = ProductsModel(
            ProductsRepository(
                ProductsDataBase.getInstance(this).productsDao()
            )
        )
        productsAdapter = ProductsAdapter(object : ProductsAdapter.OnProductClicked {
            override fun onDeleteClicked(product: Product) {
                //to add a confirmation dialog
                val dialog = makeDialog(
                    this@ProductsActivity,
                    title = "suppression",
                    message = "supprimer ?",
                    onDialogClicked = object : OnDialogClicked {
                        override fun onPositiveButtonClicked() {
                            viewModel.deleteProduct(product)
                        }

                        override fun onNegativeButtonClicked() {
                            Log.i(TAG, "onNegativeButtonClicked canceled delete")
                        }
                    }
                )
                dialog.show()

            }

            override fun onUpdateClicked(product: Product) {
                val dialog = PriceEditorDialog(
                    this@ProductsActivity,
                    title = "Modification",
                    message = "modifier ?",
                    initialPrice = product.price,
                    onPriceSubmit = object : OnPriceSubmit {
                        override fun onPositive(price: String) {
                            Log.i(TAG, "onPositive: $price")
                            product.price = price
                            viewModel.updateProduct(product)
                        }

                        override fun onNegative() {
                            Log.i(TAG, "onNegativeButtonClicked canceled update")
                        }
                    }
                )
                dialog.show(supportFragmentManager, PriceEditorDialog.TAG)
            }
        })


        binding.productsRv.apply {
            adapter = productsAdapter
            layoutManager = LinearLayoutManager(this@ProductsActivity)
        }

        viewModel.apply {
            productsList.observe(this@ProductsActivity) { productsList ->
                productsAdapter.setProductsList(productsList)
            }
            binding.addProduct.setOnClickListener {
                viewModel.scanProductAndResult(this@ProductsActivity, object : OnScanProduct {
                    override fun onSuccess(barcode: Barcode) {
                        val dialog = PriceEditorDialog(
                            this@ProductsActivity,
                            title = getString(R.string.add_product_title),
                            message = getString(R.string.add_product_message),
                            onPriceSubmit = object : OnPriceSubmit {
                                override fun onPositive(price: String) {
                                    val newProduct = Product(
                                        codeBar = barcode.rawValue.toString(),
                                        price = price
                                    )
                                    addProduct(newProduct)
                                }

                                override fun onNegative() {
                                    Log.d(TAG, "canceled dialog")
                                }

                            }
                        )

                        dialog.show(supportFragmentManager, PriceEditorDialog.TAG)
                    }

                    override fun onFail(e: Exception) {
                        Log.e(TAG, "onFail: ${e.message}")
                        this@ProductsActivity.toast(
                            e.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                    }

                    override fun onCanceled() {
                        Log.i(TAG, "onCanceled scanning")
                    }

                })
            }
        }

        setContentView(binding.root)
    }
}