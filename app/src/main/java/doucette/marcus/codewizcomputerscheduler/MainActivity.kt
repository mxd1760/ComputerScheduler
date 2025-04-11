package doucette.marcus.codewizcomputerscheduler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import doucette.marcus.codewizcomputerscheduler.ui.ComputerSelectorScreen.ComputerSelectorScreen
import doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView.TimeSlotView
import doucette.marcus.codewizcomputerscheduler.ui.theme.CodewizComputerSchedulerTheme

class MainActivity : ComponentActivity() {
    companion object{
        const val LOG_TAG = "Computer_Scheduler"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodewizComputerSchedulerTheme {
                TimeSlotView()
            }
        }
    }
}

