package doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doucette.marcus.codewizcomputerscheduler.data.DataService
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek


data class TimeSlotState(
    val timeSlots:List<TimeSlotViewData>,
    val listState:LazyListState=LazyListState(Int.MAX_VALUE / 2)
)

data class TimeSlotViewData(
    val day:DayOfWeek,
    val time:Int,
    val students:List<StudentCard>
)

data class StudentCard(
    val name:String,
    val subject:String,
    val computer:String,
)

sealed interface TimeSlotViewAction{

    data class SetTSI(val newIndex:Int): TimeSlotViewAction
    data object OpenSettings: TimeSlotViewAction
    data object AddStudent: TimeSlotViewAction
    data class EditStudent(val index:Int):TimeSlotViewAction
}

class TimeSlotViewModel: ViewModel() {
    val ds = DataService.get()
    private val _state = MutableStateFlow(TimeSlotState(ds.getTimeSlotViewData()))
    val state = _state.asStateFlow()

    fun ActionHandler(action: TimeSlotViewAction){
        when(action){
            TimeSlotViewAction.AddStudent -> {
                TODO()
            }
            TimeSlotViewAction.OpenSettings -> {
                TODO()
            }
            is TimeSlotViewAction.SetTSI -> {
                viewModelScope.launch{
                    _state.value.listState.scrollToItem(action.newIndex)
                }
            }

            is TimeSlotViewAction.EditStudent -> TODO()
        }
    }
}