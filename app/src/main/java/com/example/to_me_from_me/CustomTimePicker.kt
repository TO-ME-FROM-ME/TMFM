package com.example.to_me_from_me

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import java.lang.reflect.Field

class CustomTimePicker : TimePicker {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        try {
            val fields = TimePicker::class.java.declaredFields
            for (field in fields) {
                if (field.type == NumberPicker::class.java) {
                    field.isAccessible = true
                    val numberPicker = field.get(this) as NumberPicker?
                    if (numberPicker != null) {
                        val numberPickerFields = numberPicker::class.java.declaredFields
                        for (numberPickerField in numberPickerFields) {
                            if (numberPickerField.name == "mSelectionDivider") {
                                numberPickerField.isAccessible = true
                                numberPickerField.set(numberPicker, ContextCompat.getDrawable(context, R.drawable.custom_divider))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
