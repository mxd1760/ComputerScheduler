package doucette.marcus.codewizcomputerscheduler

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView.TimeSlotView
import doucette.marcus.codewizcomputerscheduler.ui.theme.CodewizComputerSchedulerTheme

class MainActivity : ComponentActivity() {


    companion object{
        const val LOG_TAG = "Computer_Scheduler"
        private var m_context: Context? = null
        public fun getAppContext():Context{
            return requireNotNull(m_context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        m_context = applicationContext
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodewizComputerSchedulerTheme {
                TimeSlotView()
            }
        }
    }
}

