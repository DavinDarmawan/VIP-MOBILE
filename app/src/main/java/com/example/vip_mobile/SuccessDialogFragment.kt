package com.example.vip_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class SuccessDialogFragment : DialogFragment() {

    private var onContinueListener: (() -> Unit)? = null

    fun setOnContinueListener(listener: () -> Unit) {
        onContinueListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_success_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val message = arguments?.getString("message") ?: "Berhasil!"
        view.findViewById<TextView>(R.id.tvSuccessMessage).text = message

        view.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            dismiss()
            onContinueListener?.invoke()
        }
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance(message: String): SuccessDialogFragment {
            val fragment = SuccessDialogFragment()
            val args = Bundle()
            args.putString("message", message)
            fragment.arguments = args
            return fragment
        }
    }
}
