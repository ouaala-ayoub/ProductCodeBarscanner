package com.example.barcodeapp.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.example.barcodeapp.R
import com.example.barcodeapp.databinding.DialogViewBinding
import com.example.barcodeapp.ui.viewmodels.ProductPriceInputModel

class PriceEditorDialog(
    private val context: Context,
    private val onPriceSubmit: OnPriceSubmit,
    private val title: String?,
    private val message: String?,
    private val initialPrice: String = "",
    private val initialQuantity: Int = 0,
    private val cancelable: Boolean = false,
    private val negativeText: String = context.resources.getString(R.string.Cancel),
    private val positiveText: String = context.resources.getString(R.string.Oui)
) : DialogFragment() {

    private lateinit var binding: DialogViewBinding
    private lateinit var viewModel: ProductPriceInputModel

    companion object {
        const val TAG = "PriceEditorDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogViewBinding.inflate(layoutInflater)

        binding.priceEditText.setText(initialPrice)
        binding.quantityEditText.setText(initialQuantity.toString())

        viewModel = ProductPriceInputModel(
            binding.priceEditText.text.toString(),
            binding.quantityEditText.text.toString()
        )

        val dialog = makeDialog(
            context = requireContext(),
            onDialogClicked = object : OnDialogClicked {
                override fun onPositiveButtonClicked() {
                    onPriceSubmit.onPositive(
                        binding.priceEditText.text.toString(),
                        binding.quantityEditText.text.toString()
                    )
                }

                override fun onNegativeButtonClicked() {
                    onPriceSubmit.onNegative()
                }
            },
            title = title,
            message = message,
            cancelable = cancelable,
            view = binding.root,
            negativeText = negativeText,
            positiveText = positiveText
        )
        handlePrice(dialog, binding, viewModel)
        return dialog
    }

    private fun handlePrice(
        dialog: AlertDialog,
        dialogViewBinding: DialogViewBinding,
        viewModel: ProductPriceInputModel
    ) {
        viewModel.also {
            it.isValid.observe(this) { isValidInput ->
                Log.d(TAG, "handlePrice: $isValidInput")
                dialog
                    .getButton(AlertDialog.BUTTON_POSITIVE)
                    .isEnabled = isValidInput
            }

            dialogViewBinding.priceEditText.doOnTextChanged { text, _, _, _ ->
                it.setThePrice(text.toString())
            }

            dialogViewBinding.quantityEditText.doOnTextChanged { text, _, _, _ ->
                it.setTheQuantity(text.toString())
            }
        }
    }
}