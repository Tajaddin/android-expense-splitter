package com.tajaddin.splitter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SplitCalculatorTest {

    @Test
    fun `even split has no remainder`() {
        val r = SplitCalculator.split(billCents = 1000, people = 4, tipPercent = 0)
        assertEquals(listOf(250, 250, 250, 250), r.perPersonCents)
        assertTrue(r.isExact)
    }

    @Test
    fun `remainder cents go to the first people`() {
        // 1000 / 3 = 333 r1 -> [334, 333, 333]
        val r = SplitCalculator.split(billCents = 1000, people = 3, tipPercent = 0)
        assertEquals(listOf(334, 333, 333), r.perPersonCents)
        assertEquals(1000, r.perPersonCents.sum())
    }

    @Test
    fun `tip is rounded half up`() {
        assertEquals(180, SplitCalculator.tipCents(1000, 18))   // 1000 * .18 = 180
        assertEquals(7, SplitCalculator.tipCents(45, 15))       // 6.75 -> 7
        assertEquals(0, SplitCalculator.tipCents(1000, 0))
    }

    @Test
    fun `total includes tip`() {
        val r = SplitCalculator.split(billCents = 1000, people = 2, tipPercent = 20)
        assertEquals(1200, r.totalCents)
        assertEquals(listOf(600, 600), r.perPersonCents)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero people is rejected`() {
        SplitCalculator.split(billCents = 1000, people = 0, tipPercent = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative bill is rejected`() {
        SplitCalculator.split(billCents = -1, people = 2, tipPercent = 0)
    }

    @Test
    fun `more people than cents still sums exactly`() {
        val r = SplitCalculator.split(billCents = 3, people = 5, tipPercent = 0)
        assertEquals(listOf(1, 1, 1, 0, 0), r.perPersonCents)
        assertEquals(3, r.perPersonCents.sum())
    }

    // The hero property: across 100,000 randomized bills, the per-person split
    // always sums to the exact total. No cent is ever lost or created.
    @Test
    fun `penny-exact across 100k randomized splits`() {
        val rng = Random(7)
        var drift = 0L
        repeat(100_000) {
            val bill = rng.nextInt(0, 5_000_00)      // up to $5000
            val people = rng.nextInt(1, 30)
            val tip = rng.nextInt(0, 40)
            val r = SplitCalculator.split(bill, people, tip)
            drift += (r.totalCents - r.perPersonCents.sum()).toLong()
            assertEquals(r.totalCents, r.perPersonCents.sum())
            assertEquals(people, r.perPersonCents.size)
        }
        assertEquals(0L, drift)
    }
}
