package doucette.marcus.codewizcomputerscheduler.ui.NewEnrolmentPopup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import doucette.marcus.codewizcomputerscheduler.data.CWClass
import doucette.marcus.codewizcomputerscheduler.data.Computer
import doucette.marcus.codewizcomputerscheduler.data.DataService
import doucette.marcus.codewizcomputerscheduler.data.Student
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class NewEnrolmentState(
    val timeSlot:TimeSlot,
    val student: Student? = null,
    val computer: Computer? = null,
    val subject: CWClass? = null,
    val activePicker: EnrolmentPicker = EnrolmentPicker.None
)

enum class EnrolmentPicker{
    None,
    Student,
    Computer,
    Subject
}

class NewEnrolmentViewModelFactory(private val timeSlotId: UUID, private val closePopup:()->Unit):
    ViewModelProvider.NewInstanceFactory(){
        val timeSlot = DataService.get().getTimeSlot(timeSlotId)
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NewEnrolmentViewModel(timeSlot,closePopup) as T
}

sealed interface NewEnrolmentAction{
    data class ChangeStudent(val newStudent:Student):NewEnrolmentAction
    data class ChangeComputer(val newComputer:Computer):NewEnrolmentAction
    data class ChangeClass(val newClass:CWClass):NewEnrolmentAction
    data object Submit:NewEnrolmentAction
    data object Cancel:NewEnrolmentAction
    data class ChangeSelectionScreen(val newScreen:EnrolmentPicker):NewEnrolmentAction
}


class NewEnrolmentViewModel(timeSlot: TimeSlot,private val cancelAction:()->Unit): ViewModel(){

    private val _state = MutableStateFlow(NewEnrolmentState(timeSlot = timeSlot))
    val state = _state.asStateFlow()

    fun ActionHandler(action:NewEnrolmentAction){
        when(action){
            NewEnrolmentAction.Cancel -> {
                cancelAction()
            }
            is NewEnrolmentAction.ChangeClass -> TODO()
            is NewEnrolmentAction.ChangeComputer -> TODO()
            is NewEnrolmentAction.ChangeStudent -> TODO()
            NewEnrolmentAction.Submit -> TODO()
            is NewEnrolmentAction.ChangeSelectionScreen -> {
                _state.update {old->
                    old.copy(
                        activePicker = action.newScreen
                    )
                }
            }
        }
    }

}