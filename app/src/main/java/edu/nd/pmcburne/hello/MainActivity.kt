package edu.nd.pmcburne.hello

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.*
import edu.nd.pmcburne.hello.data.*
import edu.nd.pmcburne.hello.ui.theme.*
import edu.nd.pmcburne.hello.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {

    Column (modifier = modifier) {
        // title
        Text(
            text = "Campus Map!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            fontSize = 22.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        TagDropdown(
            tags = viewModel.tags,
            selected = viewModel.selectedTag,
            onSelected = { viewModel.onTagSelected(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        MapView(viewModel.locations)
    }
}

@Composable
fun TagDropdown(
    tags: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val sortedTags = tags.sorted()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected.ifEmpty { "core" },
                    color = DeepGreen,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "▼",
                    color = DeepGreen
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            //containerColor = LightGreen,
            onDismissRequest = { expanded = false }
        ) {
            sortedTags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(
                        tag,
                        color = DeepGreen
                        ) },
                    onClick = {
                        onSelected(tag)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MapView(locations: List<LocationEntity>) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(38.03474, -78.50820), 15f
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        locations.forEach { loc ->
            MarkerInfoWindow(
                state = MarkerState(
                    position = LatLng(loc.latitude, loc.longitude)
                ),
                title = loc.name,
                snippet = loc.description
            ){
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                        .width(200.dp)
                ) {
                    Text(
                        text = loc.name,
                        fontSize = 16.sp,
                        color = DeepGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = loc.description,
                        fontSize = 14.sp,
                        color = DeepGreen
                    )
                }
            }
        }
    }
}