package doucette.marcus.codewizcomputerscheduler.ui.NewEnrolmentPopup


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import doucette.marcus.codewizcomputerscheduler.ui.theme.CodewizComputerSchedulerTheme
import java.time.DayOfWeek
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.statusBars
import java.util.UUID


@Composable
fun NewEnrolmentPopup(tsId:UUID, closePopup:()->Unit,  modifier: Modifier = Modifier) {
    val vm:NewEnrolmentViewModel = viewModel(factory=NewEnrolmentViewModelFactory(tsId, closePopup))
    val state by vm.state.collectAsStateWithLifecycle()
    DumbNewEnrolmentPopup(vm::ActionHandler,state,modifier=modifier)
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DumbNewEnrolmentPopup(action:(NewEnrolmentAction)->Unit,state:NewEnrolmentState,  modifier: Modifier = Modifier) {
    Box(modifier=Modifier.fillMaxSize()){
        val curDns = LocalDensity.current
        Column(modifier=modifier
            .align(Alignment.Center)
            .fillMaxSize()
            .padding(
                top = with(curDns){WindowInsets.statusBars.getTop(curDns).toDp()},
                bottom = with(curDns){WindowInsets.navigationBarsIgnoringVisibility.getBottom(curDns).toDp()}
            )
            .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            Column(
                modifier=Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.Gray)
                    .padding(20.dp)
            ){
                Text(state.timeSlot.LabelString(),
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Cyan)
                        .padding(5.dp)
                )
                Button(
                    onClick={action(NewEnrolmentAction.ChangeSelectionScreen(EnrolmentPicker.Student))},
                    modifier=Modifier.fillMaxWidth()
                ){
                    Text(state.student?.name?:"None")
                }
                Button(
                    onClick={action(NewEnrolmentAction.ChangeSelectionScreen(EnrolmentPicker.Computer))},
                    modifier=Modifier.fillMaxWidth()
                ){
                    Text(state.computer?.name?:"None")
                }

            }
            Row(
                modifier=Modifier
                    .fillMaxWidth()
            ){
                Button(
                    onClick={action(NewEnrolmentAction.Cancel)},
                    colors= ButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black
                    ),
                    modifier=Modifier.weight(1f).height(100.dp)
                ){
                    Text("Cancel")
                }
                Button(
                    onClick={},
                    colors=ButtonColors(
                        containerColor = Color.Green,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black
                    ),
                    modifier=Modifier.weight(1f).height(100.dp)
                ){
                    Text("Submit")
                }
            }
        }

        when(state.activePicker){
            EnrolmentPicker.None -> {}
            EnrolmentPicker.Student -> {

            }
            EnrolmentPicker.Computer -> {

            }
            EnrolmentPicker.Subject -> {

            }
        }
    }
}


@Preview
@Composable
private fun NewEnrolmentPopupPreview() {
    CodewizComputerSchedulerTheme {
        DumbNewEnrolmentPopup({}, NewEnrolmentState(
            TimeSlot(
                UUID.randomUUID(),
                DayOfWeek.MONDAY,
                12,
            )
        )
        )
    }

}