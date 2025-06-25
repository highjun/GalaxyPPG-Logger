package kaist.iclab.galaxyppglogger.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.galaxyppglogger.convert2Datetime
import kaist.iclab.galaxyppglogger.data.WearableNodeInfo
import kaist.iclab.galaxyppglogger.formatLapsedTime
import kaist.iclab.galaxyppglogger.toHrStatus
import kaist.iclab.galaxyppglogger.toPpgStatus
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.util.Locale

@Composable
fun MainScreen(
    viewModel: AbstractViewModel = koinViewModel()
) {
    val events = viewModel.eventsState.collectAsState().value
    val isMonitoring = viewModel.isMonitoringState.collectAsState()
    val lapsedTime = viewModel.lapsedTime.collectAsState()
    val wearableStat = viewModel.wearableStatState.collectAsState().value
    val wearableNodeInfo = viewModel.connectedNodeInfo.collectAsState().value

    Log.d("MainScreen", "wearableStat: ${wearableStat}, events: ${events.size}")
    Scaffold(
        topBar = {
            MyAppBarWithMenu(
                onExportClick = {
                    viewModel.export()
                },
                onResetClick = {
                    viewModel.reset()
                }
            )
        },
        modifier = Modifier.padding(0.dp),
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(innerPadding)
        ) {
            /* Wearable Node Info */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Wearable Node Info",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = if(wearableNodeInfo == null) "Disconnected" else "${wearableNodeInfo.displayName} (${wearableNodeInfo.nodeId})",
                    fontSize = 16.sp,
                    color = if (wearableNodeInfo == null) Color.Red else Color.Black
                )

            }
            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            /* Collector Controller & Timer */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    onClick = { if (isMonitoring.value) viewModel.stop() else viewModel.start() }) {
                    Text(text = if (isMonitoring.value) "STOP" else "START", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Timer: ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = lapsedTime.value.formatLapsedTime(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            /* Retrieved Data */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Last Update: ${wearableStat.timestamp.convert2Datetime()}",
                    fontSize = 16.sp
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "ACC = (X = %.2f g, Y = %.2f g, Z = %.2f g)",
                        wearableStat.accX,
                        wearableStat.accY,
                        wearableStat.accZ
                    ), fontSize = 16.sp
                )
                Text(
                    text = "PPG = ${DecimalFormat("#,###").format(wearableStat.ppg)} (${wearableStat.ppgStatus.toPpgStatus()})",
                    fontSize = 16.sp
                )
                Text(
                    text = "HR = ${wearableStat.hr} BPM (${wearableStat.hrStatus.toHrStatus()})",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(8.dp))

            /* Event Tagging */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    onClick = { viewModel.tag() }) {
                    Text(text = "TAG", fontSize = 16.sp)
                }

                LazyColumn {
                    items(events, key = { it.id }) { event ->
                        Text("${event.id}: ${event.timestamp.convert2Datetime()}")
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBarWithMenu(
    onExportClick: () -> Unit,
    onResetClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Column {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = Color.Black,
            ),
            title = { Text("Galaxy PPG Logger", fontWeight = FontWeight.SemiBold) },
            actions = {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Export") },
                        onClick = {
                            menuExpanded = false
                            onExportClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Reset") },
                        onClick = {
                            menuExpanded = false
                            onResetClick()
                        }
                    )
                }
            },
            windowInsets = WindowInsets(top = 0)
        )
        Spacer(modifier = Modifier.padding(bottom = 12.dp))
    }

}