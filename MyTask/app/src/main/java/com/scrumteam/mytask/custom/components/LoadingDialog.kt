package com.scrumteam.mytask.custom.components

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scrumteam.mytask.databinding.LoadingDialogBinding

class LoadingDialog(context: Context) {
    private var _binding: LoadingDialogBinding? = null
    private val binding get() = _binding as LoadingDialogBinding

    private var dialog: AlertDialog

    init {
        _binding = LoadingDialogBinding.inflate(LayoutInflater.from(context))
        val builder = MaterialAlertDialogBuilder(context)
            .setView(binding.root)
            .setCancelable(false)
        dialog = builder.create()
    }

    fun showDialog() {
        dialog.show()
    }

    fun hideDialog() {
        dialog.cancel()
        _binding = null
    }

}