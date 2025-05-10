package doucette.marcus.codewizcomputerscheduler.ui.NewEnrolmentPopup


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
            .background(Color.White)
            .padding(
                top = with(curDns) { WindowInsets.statusBars.getTop(curDns).toDp() },
                bottom = with(curDns) {
                    WindowInsets.navigationBarsIgnoringVisibility.getBottom(
                        curDns
                    ).toDp()
                }
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
                ItemPicker("Student: ",
                    state.student?.name,
                    state.allStudents.map{Pair(it.name,Color.Green)},
                    {action(NewEnrolmentAction.ChangeStudent(state.allStudents[it]))}
                ){
                    Dialog(
                        onDismissRequest = it
                    ){
                        var name by remember{mutableStateOf("")}
                        Column(
                            modifier=Modifier
                                .size(height = 400.dp,width=250.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Blue)
                                .padding(20.dp),
                        ){
                            Text("New Student",fontWeight= FontWeight.Bold)
                            Row{
                                Text("Name: ")
                                TextField(singleLine = true,
                                    value = name,
                                    onValueChange = {name=it},
                                    modifier=Modifier.weight(1f))
                            }
                            Spacer(modifier=Modifier.weight(1f))
                            Row{
                                TextButton(onClick=it) {
                                    Text("Cancel", color=Color.Red)
                                }
                                TextButton(onClick={
                                    action(NewEnrolmentAction.CreateStudent(name))
                                    it()
                                }){
                                    Text("Submit", color=Color.Green)
                                }
                            }
                        }
                    }
                }
                ItemPicker("Computer: ",
                    state.computer?.name,
                    state.relevantComputers.map{Pair(it.name,
                        when(it.availability){
                            ComputerAvailability.Free -> Color.Green
                            ComputerAvailability.Taken -> Color.Red
                            ComputerAvailability.Adjacent -> Color.Yellow
                        }
                    )},
                    {action(NewEnrolmentAction.ChangeComputer(state.relevantComputers[it]))}){
                    Dialog(
                        onDismissRequest = it
                    ){
                        var name by remember{mutableStateOf("")}
                        Column(
                            modifier=Modifier
                                .size(height = 400.dp,width=250.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Blue)
                                .padding(20.dp),
                        ){
                            Text("New Computer",fontWeight= FontWeight.Bold)
                            Row{
                                Text("Name: ")
                                TextField(singleLine = true,
                                    value = name,
                                    onValueChange = {name=it},
                                    modifier=Modifier.weight(1f))
                            }
                            Spacer(modifier=Modifier.weight(1f))
                            Row{
                                TextButton(onClick=it) {
                                    Text("Cancel", color=Color.Red)
                                }
                                TextButton(onClick={
                                    action(NewEnrolmentAction.CreateComputer(name))
                                    it()
                                }){
                                    Text("Submit", color=Color.Green)
                                }
                            }
                        }
                    }
                }
                ItemPicker("Class: ",
                    state.currentClass?.subject,
                    state.allClasses.map{Pair(it.subject,Color.Green)},
                    {action(NewEnrolmentAction.ChangeClass(state.allClasses[it]))}){
                    Dialog(
                        onDismissRequest = it
                    ){
                        var name by remember{mutableStateOf("")}
                        Column(
                            modifier=Modifier
                                .size(height = 400.dp,width=250.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Blue)
                                .padding(20.dp),
                        ){
                            Text("New Class",fontWeight= FontWeight.Bold)
                            Row{
                                Text("Subject: ")
                                TextField(singleLine = true,
                                    value = name,
                                    onValueChange = {name=it},
                                    modifier=Modifier.weight(1f))
                            }
                            Spacer(modifier=Modifier.weight(1f))
                            Row{
                                TextButton(onClick=it) {
                                    Text("Cancel", color=Color.Red)
                                }
                                TextButton(onClick={
                                    action(NewEnrolmentAction.CreateClass(name))
                                    it()
                                }){
                                    Text("Submit", color=Color.Green)
                                }
                            }
                        }
                    }
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
                    modifier=Modifier
                        .weight(1f)
                        .height(100.dp)
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
                    modifier=Modifier
                        .weight(1f)
                        .height(100.dp)
                ){
                    Text("Submit")
                }
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

typealias BaseCallback = ()->Unit
typealias ComposableCallbackConsumer = @Composable ( BaseCallback ) -> Unit

@Composable
fun ItemPicker(text:String,
               selection:String?,
               options:List<Pair<String,Color>>,
               onChange:(Int)->Unit,
               modifier: Modifier = Modifier,
               createComposable:ComposableCallbackConsumer
) {
    var show_create_popup by remember{mutableStateOf(false)}
    var show_popup by remember{mutableStateOf(false)}
    Row(verticalAlignment = Alignment.CenterVertically){
        Text(text,fontSize = 30.sp,modifier=Modifier.weight(1f))
        Button(
            onClick={show_popup = true},
        ){
            Text(selection?:"None")
        }
    }
    if (show_popup) {
       Dialog (
           onDismissRequest = {show_popup=false}
       ){
           Column(modifier=Modifier
               .size(height = 400.dp,width=250.dp)
               .clip(RoundedCornerShape(20.dp))
               .background(Color.Cyan)
               .padding(20.dp),
               horizontalAlignment = Alignment.CenterHorizontally
           ){
               LazyColumn(
                   modifier=Modifier.weight(1f),
                   horizontalAlignment = Alignment.CenterHorizontally
               ){
                   items(options.size){
                       Button(
                           onClick={
                               onChange(it)
                               show_popup=false},
                           colors = ButtonColors(
                               options[it].second,
                               Color.Black,Color.Black,Color.Black
                           )
                       ){
                           Text(options[it].first)
                       }
                   }
                   item{
                       Button(onClick = {show_create_popup=true}){
                           Text("Add New")
                       }
                   }

               }
               Button(onClick = {show_popup=false}){
                   Text("Close")
               }
           }
       }
    }
    if (show_create_popup){
        createComposable{show_create_popup=false}
    }
}