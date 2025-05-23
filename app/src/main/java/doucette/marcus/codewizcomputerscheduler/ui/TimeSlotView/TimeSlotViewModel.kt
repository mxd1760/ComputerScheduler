package doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import doucette.marcus.codewizcomputerscheduler.data.DataService
import doucette.marcus.codewizcomputerscheduler.data.Enrolment
import doucette.marcus.codewizcomputerscheduler.data.StudentCard
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek


data class TimeSlotState(
    val timeSlots:List<TimeSlotViewData>,
    val listState:LazyListState=LazyListState(Int.MAX_VALUE / 2),
    val currentPopup:TSPopupType=TSPopupType.NONE,
    val selectedEnrollment: Enrolment? = null
)

enum class TSPopupType{
    NONE,
    NEW_ENROLLMENT,
    SETTINGS,
    EDIT_ENROLLMENT,
    NEW_TIME_SLOT,
}

data class TimeSlotViewData(
    val timeSlot:TimeSlot,
    val students:List<StudentCard>
){
    fun LabelString():String{
        return "${timeSlot.day.toString().take(3)} ${timeSlot.timeOfDay}:00"
    }
}



sealed interface TimeSlotViewAction{

    data class SetTSI(val newIndex:Int): TimeSlotViewAction
    data object OpenSettings: TimeSlotViewAction
    data object AddStudent: TimeSlotViewAction
    data class EditStudent(val enrollment:Enrolment):TimeSlotViewAction
    data object ClosePopup:TimeSlotViewAction
    data object OpenNewTimeSlotPopup : TimeSlotViewAction

    data class AddTimeSlot(val day:DayOfWeek,val time:Int):TimeSlotViewAction
    data class DeleteTimeSlot(val timeSlotViewData: TimeSlotViewData) : TimeSlotViewAction
}

class TimeSlotViewModel: ViewModel() {
    val ds = DataService.get()
    private val _state = MutableStateFlow(TimeSlotState(emptyList()))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO){
            _state.update { old->
                old.copy(
                    timeSlots = ds.getTimeSlotViewData()
                )
            }
        }
    }

    fun ActionHandler(action: TimeSlotViewAction){
        when(action){
            TimeSlotViewAction.AddStudent -> {
                _state.update { old->
                    old.copy(
                        currentPopup = TSPopupType.NEW_ENROLLMENT
                    )
                }
            }
            TimeSlotViewAction.ClosePopup -> {
                viewModelScope.launch(Dispatchers.IO){
                    _state.update { old->
                        old.copy(
                            timeSlots = ds.getTimeSlotViewData(),
                            currentPopup = TSPopupType.NONE
                        )
                    }
                }
            }
            TimeSlotViewAction.OpenSettings -> {
                TODO()
            }
            is TimeSlotViewAction.SetTSI -> {
                viewModelScope.launch{
                    _state.value.listState.scrollToItem(action.newIndex)
                }
            }

            is TimeSlotViewAction.EditStudent ->{
                _state.update{old->
                    old.copy(
                        selectedEnrollment = action.enrollment,
                        currentPopup = TSPopupType.EDIT_ENROLLMENT
                    )
                }
            }
            is TimeSlotViewAction.AddTimeSlot ->{
                viewModelScope.launch(Dispatchers.IO){
                    ds.createTimeSlot(action.day,action.time)
                    _state.update { old->
                        old.copy(
                            timeSlots = ds.getTimeSlotViewData()
                        )
                    }
                }
            }
            TimeSlotViewAction.OpenNewTimeSlotPopup ->{
                _state.update { old->
                    old.copy(
                        currentPopup = TSPopupType.NEW_TIME_SLOT
                    )
                }
            }
            is TimeSlotViewAction.DeleteTimeSlot -> {
                viewModelScope.launch(Dispatchers.IO) {
                    ds.deleteTimeSlot(action.timeSlotViewData.timeSlot.id)
                    _state.update { old ->
                        old.copy(
                            timeSlots = ds.getTimeSlotViewData()
                        )
                    }
                }
            }
        }
    }
}