package com.tajaddin.splitter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SplitUiState(
    val billInput: String = "",
    val people: Int = 2,
    val tipPercent: Int = 18,
    val result: SplitResult? = null,
    val error: String? = null,
)

/** Holds the splitter screen state and recomputes on every input change. */
class SplitViewModel : ViewModel() {
    private val _state = MutableStateFlow(SplitUiState())
    val state: StateFlow<SplitUiState> = _state.asStateFlow()

    fun onBillChange(text: String) {
        _state.value = _state.value.copy(billInput = text)
        recompute()
    }

    fun onPeopleChange(people: Int) {
        if (people < 1) return
        _state.value = _state.value.copy(people = people)
        recompute()
    }

    fun onTipChange(tipPercent: Int) {
        if (tipPercent < 0) return
        _state.value = _state.value.copy(tipPercent = tipPercent)
        recompute()
    }

    private fun recompute() {
        val s = _state.value
        val cents = parseDollarsToCents(s.billInput)
        if (cents == null) {
            _state.value = s.copy(result = null, error = if (s.billInput.isBlank()) null else "Enter a valid amount")
            return
        }
        _state.value = s.copy(result = SplitCalculator.split(cents, s.people, s.tipPercent), error = null)
    }

    companion object {
        /** Parse a dollar string ("12.34", "12", " 12.3 ") to integer cents. */
        fun parseDollarsToCents(text: String): Int? {
            val t = text.trim()
            if (t.isEmpty()) return null
            val m = Regex("^\\$?(\\d+)(?:\\.(\\d{1,2}))?$").matchEntire(t) ?: return null
            val dollars = m.groupValues[1].toLongOrNull() ?: return null
            val fraction = m.groupValues[2]
            val cents = when (fraction.length) {
                0 -> 0
                1 -> fraction.toInt() * 10
                else -> fraction.toInt()
            }
            val total = dollars * 100 + cents
            if (total > Int.MAX_VALUE) return null
            return total.toInt()
        }

        fun formatCents(cents: Int): String = "$%d.%02d".format(cents / 100, cents % 100)
    }
}
