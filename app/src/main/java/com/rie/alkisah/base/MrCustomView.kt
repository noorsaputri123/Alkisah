package com.rie.alkisah.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rie.alkisah.R

//Noor Saputri

class MrCustomView : AppCompatEditText, View.OnTouchListener {
    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private lateinit var eyeIcon: Drawable
    private lateinit var key: Drawable
    private var isError = false
    private var isPassword: Boolean = false
    private var isEmailRegister: Boolean = false
    private var isPasswordRegister: Boolean = false
    private var isConfirmPassword: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isPassword || isPasswordRegister || isConfirmPassword) {
            showDrawable()
        }
        background = if (isError) editTextErrorBackground else editTextBackground
    }

    private fun init() {
        val password: MrCustomView? = findViewById(R.id.ed_login_password)
        password?.isPassword = true
        val emailRegister: MrCustomView? = findViewById(R.id.ed_register_email)
        emailRegister?.isEmailRegister  = true
        val passwordRegister: MrCustomView? = findViewById(R.id.ed_register_password)
        passwordRegister?.isPasswordRegister = true
        val confrimPassword: MrCustomView? = findViewById(R.id.ed_register_confirmPass)
        confrimPassword?.isConfirmPassword = true
        editTextBackground = ContextCompat.getDrawable(context, R.drawable.style_bg_edittext) as Drawable
        editTextErrorBackground = ContextCompat.getDrawable(context, R.drawable.style_edittext_error) as Drawable
        eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye_off) as Drawable
        key = ContextCompat.getDrawable(context, R.drawable.ic_key) as Drawable

        if (isPassword || isPasswordRegister || isConfirmPassword) {
            setOnTouchListener(this)
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                    if (isPassword || isPasswordRegister || isConfirmPassword) {
                    if (s.toString().isNotEmpty() && s.toString().length < 8) {
                        error = resources.getString(R.string.password_minimum_character)
                        isError = true
                    } else {
                        error = null
                        isError = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }


    private fun showDrawable() {
        val paddingEnd = (8 * resources.displayMetrics.density).toInt()
        val lockWithPadding = InsetDrawable(key, 0, 0, paddingEnd, 0)
        setDrawables(startOfTheText = lockWithPadding, endOfTheText = eyeIcon)
    }

    private fun hideEyeButton() {
        setDrawables()
    }

    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    companion object {
        const val DRAWABLE_RIGHT = 2
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawablesRelative[DRAWABLE_RIGHT] != null) {
            val eyeButtonStart: Float
            val eyeButtonEnd: Float
            var isEyeButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                eyeButtonEnd = (eyeIcon.intrinsicWidth + paddingStart).toFloat()
                if (event.x < eyeButtonEnd) isEyeButtonClicked = true
            } else {
                eyeButtonStart = (width - paddingEnd - eyeIcon.intrinsicWidth).toFloat()
                if (event.x > eyeButtonStart) isEyeButtonClicked = true
            }

            if (isEyeButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hideEyeButton()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod = PasswordTransformationMethod.getInstance()
                            eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye_off) as Drawable
                            showDrawable()
                        } else {
                            transformationMethod = HideReturnsTransformationMethod.getInstance()
                            eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye) as Drawable
                            showDrawable()
                        }
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }
}

