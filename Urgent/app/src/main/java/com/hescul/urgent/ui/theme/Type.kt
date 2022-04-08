package com.hescul.urgent.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hescul.urgent.R

// Avenir Font family
private val AvenirFontFamily = FontFamily(
    Font(R.font.avenir_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.avenir_demi, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(R.font.avenir_regular, weight = FontWeight.Normal, style = FontStyle.Normal)
)

// Set of Material typography styles for Urgent
val UrgentTypography = Typography(
    defaultFontFamily = AvenirFontFamily,
    h1 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp
    ),
    h2 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 48.sp
    ),
    h3 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp
    ),
    h4 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    h5 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    caption = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    button = TextStyle(
        fontFamily = AvenirFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)