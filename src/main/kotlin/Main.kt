import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*

@Composable
@Preview
fun App() = run {
    var text by remember { mutableStateOf("Hello, World!") }
    val tracker = remember { TrackingSimulator }
    val coroutineScope = rememberCoroutineScope()
    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
    coroutineScope.launch(Dispatchers.IO) {
        tracker.runSimulation()
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
