package com.example.barcodeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barcodeapp.R
import com.example.barcodeapp.data.models.Product
import com.example.barcodeapp.databinding.SingleProductBinding

class ProductsAdapter(private val onProductClicked: OnProductClicked) :
    RecyclerView.Adapter<ProductsAdapter.ProductsHolder>() {

    private var productsList: List<Product> = listOf()

    interface OnProductClicked {
        fun onDeleteClicked(product: Product)
        fun onUpdateClicked(product: Product)
    }

    inner class ProductsHolder(private val binding: SingleProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val currentProduct = productsList[position]
            
            binding.apply {

                productPrice.text = root.resources.getString(R.string.price, currentProduct.price)
                productBarcode.text = currentProduct.codeBar

                delete.setOnClickListener {
                    onProductClicked.onDeleteClicked(currentProduct)
                }
                wholeProduct.setOnClickListener {
                    onProductClicked.onUpdateClicked(currentProduct)
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