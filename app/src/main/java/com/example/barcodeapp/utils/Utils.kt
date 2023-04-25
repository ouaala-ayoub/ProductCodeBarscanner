package com.example.barcodeapp.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import com.example.barcodeapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

private const val TAG = "UtilsFile"

interface OnPriceSubmit {
    fun onPositive(price: String)
    fun onNegative()
}

interface OnDialogClicked {
    fun onPositiveButtonClicked()
    fun onNegativeButtonClicked()
}

interface OnScanProduct {
    fun onSuccess(barcode: Barcode)
    fun onFail(e: Exception)
    fun onCanceled()
}

fun Context.toast(message: String, length: Int) =
    Toast.makeText(this, message, length).show()

fun makeSnackBar(
    view: View,
    message: String,
    duration: Int
): Snackbar {
    return Snackbar.make(view, message, duration)
}

fun setTheUploadImage(
    resultLauncher: ActivityResultLauncher<Intent>,
    allowMultiple: Boolean = false
) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "image/*"
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
    resultLauncher.launch(intent)
}

fun showInContextUI(context: Context, onDialogClicked: OnDialogClicked) {
    makeDialog(
        context,
        onDialogClicked,
        context.getString(R.string.permission_required),
        context.getString(R.string.you_cant),
        negativeText = context.getString(R.string.no_thanks),
        positiveText = context.getString(R.string.authorise)

    ).show()
}

fun makeDialog(
    context: Context,
    onDialogClicked: OnDialogClicked,
    title: String?,
    message: String?,
    cancelable: Boolean = false,
    view: View? = null,
    negativeText: String = context.resources.getString(R.string.Cancel),
    positiveText: String = context.resources.getString(R.string.Oui)

): AlertDialog {
    val myDialog = AlertDialog
        .Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setView(view)
        .setCancelable(cancelable)
        .setPositiveButton(positiveText) { _, _ ->
            onDialogClicked.onPositiveButtonClicked()
//            onDialogClicked.
        }
        .setNegativeButton(negativeText) { _, _ ->
            onDialogClicked.onNegativeButtonClicked()
        }
        .create()

    myDialog.setOnCancelListener {
        it.dismiss()
    }

    return myDialog
}

fun scanProduct(context: Context, onScanProduct: OnScanProduct) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8
        )
        .build()
    val scanner = GmsBarcodeScanning.getClient(context, options)

    scanner.startScan()
        .addOnSuccessListener { barcode ->
            onScanProduct.onSuccess(barcode)
        }
        .addOnFailureListener {
            Log.d(TAG, "error: ${it.printStackTrace()}")
            onScanProduct.onFail(it)
        }
        .addOnCanceledListener {
            onScanProduct.onCanceled()
        }
}

inline fun <reified T> goToActivity(context: Context) {
    val intent = Intent(context, T::class.java)
    context.startActivity(intent)
}

