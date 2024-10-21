package com.example.mcommerce.ui.cart.view.payment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class PaymentDialogFragment : DialogFragment() {

    private var listener: CurrencySelectionListener? = null

    interface CurrencySelectionListener {
        fun onCurrencySelected(currency: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CurrencySelectionListener) {
            listener = context
        } else if (parentFragment is CurrencySelectionListener) {
            listener = parentFragment as CurrencySelectionListener
        } else {
            throw RuntimeException("$context must implement CurrencySelectionListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Choose payment method")
            .setItems(arrayOf("cash on delivery", "visa")) { dialog, which ->
                val selectedCurrency = if (which == 0) "cash" else "visa"
                listener?.onCurrencySelected(selectedCurrency)
            }
        return builder.create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}