package com.yilong.mvp

import android.text.Editable

/**
 */
fun toEditable(str: String): Editable {
    return Editable.Factory().newEditable(str)
}

