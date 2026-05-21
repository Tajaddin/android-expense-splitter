package com.tajaddin.splitter

/**
 * Penny-exact bill splitting. All money is integer cents, so there is no
 * floating-point drift. The remainder cents left after an even division are
 * distributed one-per-person to the first N people (largest-remainder), so the
 * per-person amounts always sum back to the exact total. No cent is ever lost
 * or created.
 */
data class SplitResult(
    val perPersonCents: List<Int>,
    val billCents: Int,
    val tipCents: Int,
    val totalCents: Int,
) {
    /** Invariant: the split sums exactly to the total. */
    val isExact: Boolean
        get() = perPersonCents.sum() == totalCents
}

object SplitCalculator {

    /** Rounded tip in cents (round half up). */
    fun tipCents(billCents: Int, tipPercent: Int): Int {
        require(billCents >= 0) { "bill must be non-negative" }
        require(tipPercent >= 0) { "tip percent must be non-negative" }
        return ((billCents.toLong() * tipPercent + 50) / 100).toInt()
    }

    fun split(billCents: Int, people: Int, tipPercent: Int): SplitResult {
        require(billCents >= 0) { "bill must be non-negative" }
        require(people > 0) { "people must be positive" }
        val tip = tipCents(billCents, tipPercent)
        val total = billCents + tip
        val base = total / people
        val remainder = total % people
        val per = List(people) { idx -> base + if (idx < remainder) 1 else 0 }
        return SplitResult(perPersonCents = per, billCents = billCents, tipCents = tip, totalCents = total)
    }
}
