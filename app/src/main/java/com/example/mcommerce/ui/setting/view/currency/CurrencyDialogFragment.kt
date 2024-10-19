package com.example.mcommerce.ui.setting.view.currency

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class CurrencyDialogFragment : DialogFragment() {

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
        builder.setTitle("Choose Currency")
            .setItems(arrayOf("USD", "EGP")) { dialog, which ->
                val selectedCurrency = if (which == 0) "USD" else "EGP"
                listener?.onCurrencySelected(selectedCurrency)
            }
        return builder.create()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}