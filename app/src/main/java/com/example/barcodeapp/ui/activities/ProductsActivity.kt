package com.example.barcodeapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodeapp.R
import com.example.barcodeapp.data.db.ProductsDataBase
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.models.ShowMethodEnum
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
                    initialQuantity = product.quantity,
                    barCode = product.codeBar,
                    onPriceSubmit = object : OnPriceSubmit {
                        override fun onPositive(price: String, quantity: String) {
                            Log.i(TAG, "onPositive: $price")
                            product.price = price
                            product.quantity = quantity.toInt()
                            viewModel.updateProduct(product)
                        }

                        override fun onNegative() {
                            Log.i(TAG, "onNegativeButtonClicked canceled update")
                        }
                    }
                )
                dialog.show(supportFragmentManager, PriceEditorDialog.TAG)
            }
        }, ShowMethodEnum.SHOW)


        binding.productsRv.apply {
            adapter = productsAdapter
            layoutManager = LinearLayoutManager(this@ProductsActivity)
        }

        viewModel.apply {
            productsList.observe(this@ProductsActivity) { productsList ->
                productsAdapter.setProductsList(productsList)
            }
            binding.addProduct.setOnClickListener {
                val onScan = object : OnScanProduct {
                    override fun onSuccess(barcode: Barcode) {
                        val resultBarCode = barcode.rawValue.toString()
                        val current = this
                        val dialog = PriceEditorDialog(
                            this@ProductsActivity,
                            title = getString(R.string.add_product_title),
                            message = getString(R.string.add_product_message),
                            barCode = resultBarCode,
                            onPriceSubmit = object : OnPriceSubmit {
                                override fun onPositive(price: String, quantity: String) {
                                    val newProduct = Product(
                                        codeBar = resultBarCode,
                                        price = price,
                                        quantity = quantity.toInt()
                                    )
                                    addProduct(newProduct)
                                    scanProductAndResult(this@ProductsActivity, current)
                                }

                                override fun onNegative() {
                                    Log.d(TAG, "canceled dialog")
                                }

                            }
                        )
                        showNewPriceDialog(dialog, this@ProductsActivity)
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

                }
                viewModel.scanProductAndResult(this@ProductsActivity, onScan)
            }
        }

        setContentView(binding.root)
    }

    private fun showNewPriceDialog(
        dialogFragment: PriceEditorDialog,
        fragmentActivity: FragmentActivity
    ) {
        val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(dialogFragment, PriceEditorDialog.TAG)
            .commitAllowingStateLoss()
    }
}