package doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import doucette.marcus.codewizcomputerscheduler.ui.NewEnrolmentPopup.NewEnrolmentPopup
import doucette.marcus.codewizcomputerscheduler.ui.theme.CodewizComputerSchedulerTheme
import java.time.DayOfWeek
import java.util.UUID


@Composable
fun TimeSlotView(modifier: Modifier = Modifier) {
    val vm:TimeSlotViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    DumbTimeSlotView(vm::ActionHandler,state,modifier)
}




@Composable
fun DumbTimeSlotView(action:(TimeSlotViewAction)->Unit,
                     state:TimeSlotState,
                     modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            OutlinedButton(
                onClick={action(TimeSlotViewAction.AddStudent)},
                modifier=Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            ){
                Icon(Icons.Filled.Add, contentDescription = "add",
                    modifier=Modifier.fillMaxSize())
            }
        },
        bottomBar = {
            CurrentTimeFooter(action,state)
        }
    ){ padding ->
        LazyRow(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            state = state.listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state.listState)
        ) {
            items(Int.MAX_VALUE){
                val index=if (state.timeSlots.size==0) {0} else {it%state.timeSlots.size}
                SingleClassView(action,index,state)
                VerticalDivider(modifier=Modifier.fillMaxHeight())
            }
        }
    }
    when(state.currentPopup){
        TSPopupType.NONE -> {}
        TSPopupType.NEW_ENROLLMENT -> {
            NewEnrolmentPopup(state.timeSlots[state.listState.firstVisibleItemIndex%state.timeSlots.size].timeSlot.id,
                {action(TimeSlotViewAction.ClosePopup)}
            )
        }
        TSPopupType.SETTINGS -> TODO()
        TSPopupType.EDIT_ENROLMENT -> TODO()
        TSPopupType.NEW_TIME_SLOT -> TimeSlotSelectorPopup(action,state)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentTimeFooter(action: (TimeSlotViewAction) -> Unit,
                      state:TimeSlotState,
                      modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier=modifier
            .fillMaxWidth()
            .padding(10.dp)
            .padding(bottom = 20.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(35.dp))
            .background(Color.Cyan)
    ){
        OutlinedButton (
            onClick={action(TimeSlotViewAction
                .SetTSI(state.listState.firstVisibleItemIndex-1))},
            modifier=Modifier
                .fillMaxHeight(),
            colors = ButtonColors(
                containerColor = Color.Cyan,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black
            )
        ){
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "left",
                modifier=Modifier.fillMaxHeight()
            )
        }
        val ts = if (state.timeSlots.size == 0){
            "Click Me"
        } else {
            state.timeSlots[state.listState.firstVisibleItemIndex%state.timeSlots.size].LabelString()
        }
        Text(
            ts,
            textAlign= TextAlign.Center,
            fontSize = 40.sp,
            modifier=Modifier
                .weight(1f)

                .combinedClickable {
                    action(TimeSlotViewAction.OpenNewTimeSlotPopup)
                }
        )
        OutlinedButton (
            onClick={action(TimeSlotViewAction
                .SetTSI(state.listState.firstVisibleItemIndex+1))},
            modifier=Modifier
                .fillMaxHeight(),
            colors = ButtonColors(
                containerColor = Color.Cyan,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black
            )
        ){
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "right",
                modifier=Modifier.fillMaxHeight()
            )
        }
    }
}


@Composable
fun SingleClassView(action: (TimeSlotViewAction) -> Unit,
                    index:Int,
                    state: TimeSlotState,
                    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier=modifier
            .fillMaxSize()
            .width(LocalConfiguration.current.screenWidthDp.dp)
    ){
        if (state.timeSlots.size>0) {
            items(state.timeSlots[index].students) { student ->
                ClassEntryView(action, index, student)
            }
        }
    }
}

@Composable
fun ClassEntryView(action: (TimeSlotViewAction) -> Unit,
                   index: Int,
                   student: StudentCard,
                   modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier=modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Gray)
            .padding(5.dp)
            .height(IntrinsicSize.Max)
    ){
        Column(modifier=Modifier.weight(1f)){
            Text(student.name, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("Class: ${student.subject}",)
            Text("Computer: ${student.computer}")
        }
        Button(
            onClick = {action(TimeSlotViewAction.EditStudent(index))},
            colors = ButtonColors(
                containerColor = Color.Gray,
                contentColor = Color.Black,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color.Gray
            ),
            modifier=modifier.fillMaxHeight()
        ){
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "options",
                modifier=Modifier.fillMaxHeight()
            )
        }
    }
}

@Preview
@Composable
private fun ClassPreview() {
    CodewizComputerSchedulerTheme {
        DumbTimeSlotView({},TimeSlotState(
            listOf(
                TimeSlotViewData(
                    timeSlot= TimeSlot(
                        UUID.randomUUID(),
                        DayOfWeek.MONDAY,
                        16
                    ),
                    students=listOf(
                        StudentCard(
                            name = "Billy",
                            subject = "Roblox",
                            computer = "Computer 15",
                        ),
                        StudentCard(
                            name="Mandy",
                            subject = "Minecraft Modding",
                            computer = "Computer 3"
                        ),
                        StudentCard(
                            name="Grim",
                            subject = "Godot",
                            computer = "Personal Computer"
                        )
                    )
                ),
                TimeSlotViewData(
                    timeSlot= TimeSlot(
                        UUID.randomUUID(),
                        DayOfWeek.TUESDAY,
                        16
                    ),
                    students=listOf(
                        StudentCard(
                            name="Morty",
                            subject="Unity",
                            computer="computer 3",
                        ),
                        StudentCard(
                            name="Rick",
                            subject="Arduino",
                            computer="Personal Computer",
                        ),
                        StudentCard(
                            name="Summer",
                            subject="Lego Robotics",
                            computer="No Computer Needed",
                        )
                    )

                ),
                TimeSlotViewData(
                    timeSlot= TimeSlot(
                        UUID.randomUUID(),
                        DayOfWeek.TUESDAY,
                        17
                    ),
                    students = listOf(
                        StudentCard(
                            name="Marinette",
                            subject="3D Modeling",
                            computer="Computer 12"
                        ),
                        StudentCard(
                            name="Adrien",
                            subject="Godot",
                            computer="Personal Computer"
                        ),
                        StudentCard(
                            name="Nino",
                            subject="Minecraft Modding",
                            computer="Computer 4",
                        ),
                        StudentCard(
                            name="Alya",
                            subject="Roblox",
                            computer="Computer 5"
                        )
                    )
                )
            )
        ))
    }
}