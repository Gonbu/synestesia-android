package com.billie.synestesia.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

object LogUtils {
    private const val TAG = "Synestesia"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun showErrorToast(context: Context, message: String) {
        showToast(context, message, Toast.LENGTH_LONG)
    }
}
