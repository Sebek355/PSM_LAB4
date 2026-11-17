package com.example.lab4

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kalkulatorscreen()
        }
    }
}

@Composable
fun getIosButtonColors(label: String, isDark: Boolean): Pair<Color, Color> {
    val iosOrange = Color(0xFFFF9F0A)
    val darkNumBg = Color(0xFF333333)
    val darkTopBg = Color(0xFFA5A5A5)
    val lightNumBg = Color(0xFFFFFFFF)
    val lightTopBg = Color(0xFFD1D1D6)

    return when (label) {
        "÷", "×", "-", "+", "=" -> Pair(iosOrange, Color.White)
        "C", "Del", "(", ")" -> Pair(
            if (isDark) darkTopBg else lightTopBg,
            Color.Black
        )
        else -> Pair(
            if (isDark) darkNumBg else lightNumBg,
            if (isDark) Color.White else Color.Black
        )
    }
}

@Composable
fun Kalkulatorscreen() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isDark = isSystemInDarkTheme()

    val mainBackgroundColor = if (isDark) Color.Black else Color(0xFFF2F2F7)
    val displayBgColor = if (isDark) Color(0xFF1C1C1E) else Color.White
    val displayBorderColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFD1D1D6)
    val displayTextColor = if (isDark) Color.White else Color.Black
    val secondaryTextColor = if (isDark) Color(0xFF8E8E93) else Color(0xFF6E6E73)
    val buttonBorderColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    // ZAD. 1: rememberSaveable zachowuje stan po obrocie ekranu
    var equation by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }

    val buttonRows = listOf(
        listOf("C", "Del", "(", ")"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "+", "=")
    )

    val scientificRows = listOf(
        listOf("sin", "cos", "tan", "π"),
        listOf("√", "x^y", "ln", "e")
    )

    val onButtonClick: (String) -> Unit = { label ->
        when (label) {
            "C" -> {
                equation = ""
                result = ""
            }
            "Del" -> {
                if (equation.isNotEmpty()) {
                    equation = equation.dropLast(1)
                }
            }
            "=" -> {
                val finalResult = calculateMath(equation)
                if (finalResult.isNotEmpty() && finalResult != "Błąd") {
                    equation = finalResult
                    result = ""
                } else if (equation.isNotEmpty()) {
                    result = "Błąd"
                }
            }
            "x^y" -> equation += "^"
            "sin", "cos", "tan", "ln", "√" -> equation += "$label("
            else -> equation += label
        }

        if (label != "=" && label != "C") {
            if (equation.isEmpty()) {
                result = ""
            } else {
                val currentResult = calculateMath(equation)
                result = if (currentResult == "Błąd") "" else currentResult
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBackgroundColor)
            .safeDrawingPadding() // Chroni przed nachodzeniem na pasek stanu i gestów
    ) {
        // Wyświetlacz
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.32f)
                .padding(8.dp)
                .border(1.dp, displayBorderColor, RoundedCornerShape(16.dp))
                .background(displayBgColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "WPROWADZONE DZIAŁANIE",
                color = secondaryTextColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (equation.isEmpty()) "0" else equation,
                color = displayTextColor,
                fontSize = 34.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.End)
                    .horizontalScroll(rememberScrollState())
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "BIEŻĄCY WYNIK",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = result,
                    color = if (result == "Błąd") Color(0xFFFF453A) else displayTextColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.68f)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in scientificRows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (label in row) {
                                CalculatorButton(
                                    label = label,
                                    isDark = isDark,
                                    borderColor = buttonBorderColor,
                                    fontSize = 20,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    onClick = { onButtonClick(label) }
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in buttonRows) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (label in row) {
                                CalculatorButton(
                                    label = label,
                                    isDark = isDark,
                                    borderColor = buttonBorderColor,
                                    fontSize = 22,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    onClick = { onButtonClick(label) }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.68f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (row in buttonRows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (label in row) {
                            CalculatorButton(
                                label = label,
                                isDark = isDark,
                                borderColor = buttonBorderColor,
                                fontSize = 24,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = { onButtonClick(label) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    isDark: Boolean,
    borderColor: Color,
    fontSize: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (bgColor, txtColor) = getIosButtonColors(label, isDark)
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = txtColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
    }
}

// ZAD. 2, 3, 4: Czysta funkcja obliczeniowa (bez zależności od UI)
fun calculateMath(equation: String): String {
    if (equation.isBlank()) return ""

    return try {
        val preparedEquation = equation
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "pi")
            .replace("√", "sqrt")
            .replace("ln", "log")

        val expression = ExpressionBuilder(preparedEquation).build()
        val evalResult = expression.evaluate()

        if (evalResult.isNaN() || evalResult.isInfinite()) {
            return "Błąd"
        }

        // Zaokrąglenie eliminuje błędy zmiennoprzecinkowe typu 0.1 + 0.2 = 0.30000000000000004
        val rounded = BigDecimal.valueOf(evalResult)
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()

        rounded.toPlainString()
    } catch (e: Exception) {
        "Błąd"
    }
}