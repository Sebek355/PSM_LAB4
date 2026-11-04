package com.example.lab4

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder

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
fun Kalkulatorscreen() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var equation by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

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
                if (result.isNotEmpty() && result != "Błąd") {
                    equation = result
                    result = ""
                }
            }
            else -> {
                equation += label
            }
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

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
                .padding(8.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "wprowadzone działanie",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (equation.isEmpty()) "0" else equation,
                color = Color.Black,
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "bieżący wynik",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = result,
                    color = Color.Black,
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
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
                                Button(
                                    onClick = { onButtonClick(label) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                ) {
                                    Text(text = label, color = Color.Black, fontSize = 20.sp)
                                }
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
                                Button(
                                    onClick = { onButtonClick(label) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                ) {
                                    Text(text = label, color = Color.Black, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
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
                            Button(
                                onClick = { onButtonClick(label) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                Text(text = label, color = Color.Black, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun calculateMath(equation: String): String {
    if (equation.isEmpty()) return "" // Zabezpieczenie przed pustym działaniem

    return try {
        // Tłumaczymy znaki z przycisków na format zrozumiały dla biblioteki exp4j
        val preparedEquation = equation
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "pi")
            .replace("√", "sqrt")  // exp4j używa 'sqrt' do pierwiastków
            .replace("x^y", "^")   // potęgowanie to po prostu znak '^'
            .replace("ln", "log")  // logarytm naturalny w exp4j to 'log'

        val expression = ExpressionBuilder(preparedEquation).build()
        val result = expression.evaluate()

        if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            result.toString()
        }
    } catch (e: Exception) {
        "Błąd"
    }
}