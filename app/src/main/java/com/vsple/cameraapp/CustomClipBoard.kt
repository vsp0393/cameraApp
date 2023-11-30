package com.vsple.cameraapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi


object CustomClipBoard {

    val clipboardLabel = "CustomClipboard"

    /*@RequiresApi(Build.VERSION_CODES.P)
    fun copyTextWithFont(context: Context, textView: TextView?) {
        textView?.let {
            val originalText = it.text
            val typeface = it.typeface
            Log.d("TAG", "copyTextWithFont: "+typeface)

            // Create a SpannableString with TypefaceSpan
            val spannableString = SpannableString(originalText)
            spannableString.setSpan(TypefaceSpan(typeface), 0, originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Copy both plain text and formatted text to the clipboard
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(clipboardLabel, originalText)
            clipData.addItem(ClipData.Item(spannableString))
            clipboard.setPrimaryClip(clipData)
        }
    }*/

    fun copyTextWithFont(context: Context, plainText: CharSequence, formattedText: SpannableString) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create a ClipData object with both plain text and formatted text
        val clipData = ClipData.newPlainText("Label", plainText)
        clipData.addItem(ClipData.Item(formattedText))

        // Set the ClipData to the clipboard
        clipboardManager.setPrimaryClip(clipData)
    }
}