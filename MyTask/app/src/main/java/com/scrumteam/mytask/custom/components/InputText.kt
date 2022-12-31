package com.scrumteam.mytask.custom.components

import android.content.Context
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.scrumteam.mytask.R
import com.scrumteam.mytask.utils.Helpers.isNotValidEmail

class InputText:AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }


    private fun init() {
        doAfterTextChanged { editable ->
            when (inputType) {
                (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT) -> {
                    val emailText = editable.toString()
                    textValidateEmail(emailText)
                }
                (InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT) -> {
                    val passwordText = editable.toString()
                    textValidatePassword(passwordText)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(R.style.TextAppearance_MyTask_Body_Regular)
        }
    }

    private fun textValidatePassword(passwordText: String) {
        val layout = getTextInputLayout()
        if (passwordText.length < 6 && passwordText.isNotEmpty()) {
            layout?.let {
                it.apply {
                    error = context.getString(R.string.password_rules_length)
                    isErrorEnabled = true
                }
            }
        } else {
            layout?.let {
                it.apply {
                    error = null
                    isErrorEnabled = false
                }
            }
        }
    }

    private fun textValidateEmail(emailText: String) {
        val layout = getTextInputLayout()
        if (emailText.isNotValidEmail()) {
            layout?.let {
                it.apply {
                    error = context.getString(
                        R.string.field_no_valid,
                        context.getString(R.string.email)
                    )
                    isErrorEnabled = true
                }
            }
        } else {
            layout?.let {
                it.apply {
                    error = null
                    isErrorEnabled = false
                }
            }
        }
    }

    private fun getTextInputLayout(): TextInputLayout?{
        var parent = parent
        while (parent is View) {
            if (parent is TextInputLayout) {
                return parent
            }
            parent = parent.getParent()
        }
        return null
    }
}