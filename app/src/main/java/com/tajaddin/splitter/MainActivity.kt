package com.tajaddin.splitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SplitScreen()
            }
        }
    }
}

@Composable
fun SplitScreen(vm: SplitViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Expense Splitter", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = state.billInput,
                onValueChange = vm::onBillChange,
                label = { Text("Bill amount ($)") },
                isError = state.error != null,
                modifier = Modifier.fillMaxWidth(),
            )
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Stepper("People", state.people) { vm.onPeopleChange(it) }
            Stepper("Tip %", state.tipPercent) { vm.onTipChange(it) }

            state.result?.let { r ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tip: ${SplitViewModel.formatCents(r.tipCents)}")
                        Text("Total: ${SplitViewModel.formatCents(r.totalCents)}")
                        r.perPersonCents.forEachIndexed { i, c ->
                            Text("Person ${i + 1}: ${SplitViewModel.formatCents(c)}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Stepper(label: String, value: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("$label: $value", modifier = Modifier.padding(end = 8.dp))
        Button(onClick = { onChange(value - 1) }) { Text("-") }
        Button(onClick = { onChange(value + 1) }) { Text("+") }
    }
}
