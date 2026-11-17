package com.example.lab4

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorUnitTest {

    @Test
    fun podstawoweDzialania_zwracajaPoprawnyWynik() {
        assertEquals("5", calculateMath("2+3"))
        assertEquals("-1", calculateMath("2-3"))
        assertEquals("6", calculateMath("2×3"))
        assertEquals("2.5", calculateMath("5÷2"))
    }

    @Test
    fun kolejnoscDzialanINawiasy_saZachowane() {
        assertEquals("14", calculateMath("2+3×4"))
        assertEquals("20", calculateMath("(2+3)×4"))
        assertEquals("11", calculateMath("3+2×(1+3)"))
        assertEquals("8", calculateMath("2^3"))
    }

    @Test
    fun funkcjeNaukowe_saObliczanePoprawnie() {
        assertEquals("3", calculateMath("√(9)"))
        assertEquals("0", calculateMath("sin(0)"))
        assertEquals("1", calculateMath("cos(0)"))
        assertEquals("0", calculateMath("ln(1)"))
    }

    @Test
    fun niepoprawneWyrazenia_zwracajaBladBezAwarii() {
        assertEquals("Błąd", calculateMath("5÷0"))
        assertEquals("Błąd", calculateMath("2++2"))
        assertEquals("Błąd", calculateMath("(2+3"))
        assertEquals("Błąd", calculateMath("√(-4)"))
        assertEquals("", calculateMath(""))
    }
}