import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import shipmentFactory.Shipment


@Composable
@Preview
fun App() = run {
    val server = remember { ShipmentManager }
    val coroutineScope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    val trackedShipments = remember { mutableStateListOf<Shipment>() }
    val tracker = remember { TrackerViewHelper() }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Package ID") },
                    modifier = Modifier.weight(1f)
                )

                Button(onClick = {
                    val shipment = ShipmentManager.findShipment(text)
                    if (shipment != null && !trackedShipments.contains(shipment)) {
                        trackedShipments.add(shipment)
                        tracker.trackShipment(shipment)
                    } else {
                        errorMessage = "Invalid package ID: $text"
                        showErrorDialog = true
                    }
                }) {
                    Text("Track")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(trackedShipments) { index, shipment ->
                    ShipmentCard(index, shipment, tracker) {
                        trackedShipments.remove(shipment)
                        tracker.stopTracking(shipment)
                    }
                }
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text(errorMessage) },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            server.runServer()
        }
    }
}

@Composable
fun ShipmentCard(
    index: Int,
    shipment: Shipment,
    tracker: TrackerViewHelper,
    onStopTracking: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Package ${index + 1}: ${shipment.id}", style = MaterialTheme.typography.h6)
            Text("Status: ${tracker.status.getOrElse(index) { "N/A" } ?: "N/A"}")
            Text("Location: ${tracker.location.getOrElse(index) { "N/A" } ?: "N/A"}")
            Text("Expected Delivery: ${tracker.expectedDeliveryDate.getOrElse(index) { "N/A" } ?: "N/A"}")
            val updateHistory = tracker.updateHistory.getOrElse(index) { emptyList<String>() }
            if (updateHistory.size >= 2) {
                val lastUpdate = updateHistory[updateHistory.size - 1]
                val secondLastUpdate = updateHistory[updateHistory.size - 2]
                Text("Status Updates: Package went from $secondLastUpdate to $lastUpdate at ${tracker.timestamp.getOrElse(index) { "unknown time" } ?: "N/A"}")
            }
            Text("Notes: ${tracker.notes.getOrElse(index) { "N/A" } ?: "N/A"}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onStopTracking, modifier = Modifier.align(Alignment.End)) {
                Text("Stop Tracking")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
