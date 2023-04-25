package com.example.barcodeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.barcodeapp.R
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.data.models.ShowMethodEnum
import com.example.barcodeapp.databinding.SingleProductBinding

class ProductsAdapter(
    private val onProductClicked: OnProductClicked,
    private val showMethod: ShowMethodEnum = ShowMethodEnum.SHOW
) :
    RecyclerView.Adapter<ProductsAdapter.ProductsHolder>() {

    private var productsList: List<Product> = listOf()

    interface OnProductClicked {
        fun onDeleteClicked(product: Product)
        fun onUpdateClicked(product: Product)
        fun onPlusClicked(product: Product) = null
        fun onMinusClicked(product: Product) = null
    }

    inner class ProductsHolder(private val binding: SingleProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val currentProduct = productsList[position]

            binding.apply {

                productPrice.text =
                    root.resources.getString(R.string.price, currentProduct.price)
                productBarcode.text = currentProduct.codeBar

                delete.setOnClickListener {
                    onProductClicked.onDeleteClicked(currentProduct)
                }
                wholeProduct.setOnClickListener {
                    onProductClicked.onUpdateClicked(currentProduct)
                }
                quantity.text = currentProduct.quantity.toString()

                if (showMethod == ShowMethodEnum.EDIT) {
                    plus.setOnClickListener {
                        onProductClicked.onPlusClicked(currentProduct)
                    }
                    minus.setOnClickListener {
                        onProductClicked.onMinusClicked(currentProduct)
                    }
                } else {
                    minus.isVisible = false
                    plus.isVisible = false
                }
            }
        }
    }

    fun setProductsList(list: List<Product>) {
        productsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsHolder {
        return ProductsHolder(
            SingleProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = productsList.size

    override fun onBindViewHolder(holder: ProductsHolder, position: Int) {
        holder.bind(position)
    }
}