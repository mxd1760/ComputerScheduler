package doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.DayOfWeek

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeSlotSelectorPopup(action:(TimeSlotViewAction)->Unit, state: TimeSlotState, modifier: Modifier = Modifier) {
    var showMakeNewPopup by remember{mutableStateOf(false)}
    var showDeletePopup by remember{mutableStateOf(false)}
    var deletableTimeSlotIndex by remember{mutableStateOf(0)}
    Box(
        modifier=Modifier
            .fillMaxSize()
            .background(Color.White),
    ){
        Scaffold{innerPadding->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                Text("Time Slots", fontSize = 50.sp, fontWeight = FontWeight.Bold,
                    modifier=Modifier.fillMaxWidth().background(Color.LightGray))
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier=Modifier.weight(1f).fillMaxWidth()
                ){
                    items(state.timeSlots.size){index->
                        Row {
                            Text(
                                state.timeSlots[index].LabelString(),
                                modifier = Modifier.combinedClickable {
                                    action(TimeSlotViewAction.SetTSI(index))
                                }, fontSize = 30.sp
                            )
                            Button(
                                onClick={
                                    deletableTimeSlotIndex=index
                                    showDeletePopup=true
                                        },
                                colors=ButtonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.Black,
                                    disabledContainerColor = Color.Black,
                                    disabledContentColor = Color.Black
                                )
                            ){
                                Text("x")
                            }
                        }
                    }
                    item{
                        Button(
                            onClick={
                                showMakeNewPopup = true
                            }){
                            Text("Add Time Slot")
                        }
                    }
                }
                Button(onClick = {action(TimeSlotViewAction.ClosePopup)}){
                    Text("Back", fontSize = 35.sp)
                }
            }
        }
    }
    if (showMakeNewPopup){

        var tempDay by remember{mutableStateOf(DayOfWeek.MONDAY)}
        var tempTime by remember{mutableStateOf(8)}
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(false,false)
        ){
            Column {
                FlexMenu(
                    "Select Day ",
                    tempDay.toString(),
                    { tempDay = DAY_LIST[it] },
                    DAY_LIST.map { it.toString() })
                FlexMenu(
                    "Select Time ",
                    "$tempTime:00",
                    { tempTime = TIME_LIST[it] },
                    TIME_LIST.map { "$it:00" })
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier=Modifier.width(POPUP_WIDTH)
                ) {
                    Button(
                        onClick = { showMakeNewPopup = false },
                        colors = ButtonColors(
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Black,
                            disabledContentColor = Color.Black,
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            action(TimeSlotViewAction.AddTimeSlot(tempDay, tempTime))
                            showMakeNewPopup = false
                        },
                        colors = ButtonColors(
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Black,
                            disabledContentColor = Color.Black,
                            containerColor = Color.Green
                        )
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
    if (showDeletePopup){
        Dialog(
            onDismissRequest = {showDeletePopup=false},
        ){
            Column(modifier=Modifier.clip(RoundedCornerShape(size=30.dp)).background(Color.Yellow).padding(20.dp)) {
                Text("Are you sure you want to delete the ${state.timeSlots[deletableTimeSlotIndex].LabelString()} time slot",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                Row {
                    Button(
                        onClick = { showDeletePopup = false },
                        colors = ButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Black,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            action(TimeSlotViewAction.DeleteTimeSlot(state.timeSlots[deletableTimeSlotIndex]))
                            showDeletePopup=false
                                  },
                        colors = ButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Black,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }

}


val DAY_LIST = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)

val TIME_LIST = listOf(
    8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,
)

val POPUP_WIDTH = 300.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlexMenu(label:String,currentVal:String,changeValue:(Int)->Unit,items:List<String>,modifier: Modifier = Modifier) {
    var expanded by remember{mutableStateOf(false)}
    Row(modifier=modifier.background(Color.Gray).width(POPUP_WIDTH)){
        Text(label,modifier=Modifier.weight(1f).padding(10.dp))
        Text(currentVal,
            modifier=Modifier
                .background(Color.White)
                .width(150.dp)
                .combinedClickable {
                    expanded=true
                }.padding(10.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded=false},
        ) {
            items.forEach{it->
                DropdownMenuItem(
                    text={Text(it)},
                    onClick={
                        changeValue(items.indexOf(it))
                        expanded=false
                    }
                )
            }
        }
    }
}