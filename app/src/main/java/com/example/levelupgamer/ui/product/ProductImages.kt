package com.example.levelupgamer.ui.product

import androidx.annotation.DrawableRes
import com.example.levelupgamer.R

@DrawableRes
fun getProductImageRes(code: String): Int {
    return when (code) {
        "JM001" -> R.drawable.jm001
        "JM002" -> R.drawable.jm002
        "AC001" -> R.drawable.ac001
        "AC002" -> R.drawable.ac002
        "CO001" -> R.drawable.co001
        "CG001" -> R.drawable.cg001
        "SG001" -> R.drawable.sg001
        "MS001" -> R.drawable.ms001
        "MP001" -> R.drawable.mp001
        "PP001" -> R.drawable.pp001
        else -> R.drawable.ic_launcher_foreground // o un placeholder
    }
}
