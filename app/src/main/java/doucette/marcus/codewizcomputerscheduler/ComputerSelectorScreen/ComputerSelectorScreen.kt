package doucette.marcus.codewizcomputerscheduler.ComputerSelectorScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import doucette.marcus.codewizcomputerscheduler.ui.theme.CodewizComputerSchedulerTheme

@Composable
fun ComputerSelectorScreen(modifier: Modifier = Modifier) {
    val vm = viewModel<ComputerSelectorScreenViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    DumbComputerSelectorScreen(vm::ActionHandler,state)
}

@Composable
fun DumbComputerSelectorScreen(
    action:(ComputerSelectorScreenAction)->Unit,
    state:ComputerSelectorScreenState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier=modifier.fillMaxSize(),
        floatingActionButton = {
        Button(
            onClick={
                action(ComputerSelectorScreenAction.AddNewComputer)
            }
        ){
            Text("Add New Computer")
        }
    }){innerPadding->
        LazyColumn(modifier= Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            items(state.list.size) { index ->
                if (index!=0){
                    HorizontalDivider()
                }
                val item = state.list[index]
                ComputerEntry(item)
            }
        }
    }
}

@Composable
fun ComputerEntry(data:ComputerEntryData,modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier= modifier
            .fillMaxWidth()
            .background(if (data.is_free_now) Color.Green else Color.Yellow)
            .padding(20.dp)
    ){
        Text(data.name)
        Text(data.num_users.toString())
    }
}

@Preview
@Composable
private fun ComputerSelectorScreenPreview() {
    CodewizComputerSchedulerTheme {
        DumbComputerSelectorScreen(
            action={},
            state = ComputerSelectorScreenState(
                listOf(
                    ComputerEntryData("Computer 1", 2,true),
                    ComputerEntryData("Computer 2",5,false),
                    ComputerEntryData("Computer 3",4,true),
                    ComputerEntryData("Computer 3",4,true),
                    ComputerEntryData("Computer 3",4,false),
                    ComputerEntryData("Computer 3",4,true),
                    ComputerEntryData("Computer 3",4,true)
                )
            )
        )
    }
}