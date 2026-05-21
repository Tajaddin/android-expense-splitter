package com.tajaddin.splitter

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SplitViewModelTest {

    @Test
    fun `parses dollar strings to cents`() {
        assertEquals(1234, SplitViewModel.parseDollarsToCents("12.34"))
        assertEquals(1200, SplitViewModel.parseDollarsToCents("12"))
        assertEquals(1230, SplitViewModel.parseDollarsToCents("12.3"))
        assertEquals(500, SplitViewModel.parseDollarsToCents("$5"))
        assertEquals(1234, SplitViewModel.parseDollarsToCents("  12.34 "))
    }

    @Test
    fun `rejects invalid amounts`() {
        assertNull(SplitViewModel.parseDollarsToCents(""))
        assertNull(SplitViewModel.parseDollarsToCents("abc"))
        assertNull(SplitViewModel.parseDollarsToCents("12.345"))
        assertNull(SplitViewModel.parseDollarsToCents("-5"))
    }

    @Test
    fun `formats cents as dollars`() {
        assertEquals("$12.34", SplitViewModel.formatCents(1234))
        assertEquals("$5.00", SplitViewModel.formatCents(500))
        assertEquals("$0.09", SplitViewModel.formatCents(9))
    }

    @Test
    fun `bill input drives a result`() = runTest {
        val vm = SplitViewModel()
        vm.onBillChange("100.00")
        val s = vm.state.value
        assertNotNull(s.result)
        assertEquals(10000, s.result!!.billCents)
        assertNull(s.error)
    }

    @Test
    fun `invalid bill sets an error and clears the result`() = runTest {
        val vm = SplitViewModel()
        vm.onBillChange("100")
        assertNotNull(vm.state.value.result)
        vm.onBillChange("oops")
        assertNull(vm.state.value.result)
        assertNotNull(vm.state.value.error)
    }

    @Test
    fun `changing people and tip recomputes`() = runTest {
        val vm = SplitViewModel()
        vm.onBillChange("100.00")
        vm.onPeopleChange(4)
        vm.onTipChange(0)
        val r = vm.state.value.result!!
        assertEquals(4, r.perPersonCents.size)
        assertEquals(10000, r.totalCents)
        assertEquals(2500, r.perPersonCents[0])
    }

    @Test
    fun `people cannot go below one`() = runTest {
        val vm = SplitViewModel()
        vm.onPeopleChange(0)
        assertEquals(2, vm.state.value.people) // unchanged from default
    }
}
