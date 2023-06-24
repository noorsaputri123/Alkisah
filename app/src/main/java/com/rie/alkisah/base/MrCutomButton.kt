package com.rie.alkisah.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.rie.alkisah.R

//Noor Saputri
class MrCustomButton :  AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColor: Int = 1

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackground else disabledBackground
        setTextColor(ContextCompat.getColor(context, R.color.white))

    }

    private fun init(){
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.style_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.style_button_disabled) as Drawable
    }
}