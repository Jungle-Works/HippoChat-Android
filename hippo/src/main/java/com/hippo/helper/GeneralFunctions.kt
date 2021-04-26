package com.hippo.helper

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.TextView

/**
 * Created by gurmail on 2020-04-29.
 * @author gurmail
 */
class GeneralFunctions {
    fun spannableRetryText(textView: TextView, errorMsg: String, retry: String) {
        val spanText = SpannableStringBuilder()
        spanText.append(errorMsg)
        spanText.append(retry)
        val txtSpannable = SpannableString(spanText)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(UnderlineSpan(), spanText.length - 5, spanText.length, 0)
        txtSpannable.setSpan(boldSpan, spanText.length - 5, spanText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(txtSpannable, TextView.BufferType.SPANNABLE)
    }
}