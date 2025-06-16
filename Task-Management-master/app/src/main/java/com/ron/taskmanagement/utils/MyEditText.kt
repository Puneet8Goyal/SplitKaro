package com.ron.taskmanagement.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyEditText : AppCompatEditText {
    constructor(context: Context?) : super(context!!)

    private var listener: SelectionListener? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart != selEnd) {
            val selectedText = text.toString().substring(selStart, selEnd)
            listener?.onTextSelected(selectedText)
        }
    }

    interface SelectionListener {
        fun onTextSelected(selectString: String)
    }

    fun setSelectionListener(listener: SelectionListener){
        this.listener=listener
    }
}
