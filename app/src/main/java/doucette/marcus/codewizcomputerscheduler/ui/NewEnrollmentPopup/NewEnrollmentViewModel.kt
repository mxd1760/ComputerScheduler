package doucette.marcus.codewizcomputerscheduler.ui.NewEnrollmentPopup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import doucette.marcus.codewizcomputerscheduler.data.CWClass
import doucette.marcus.codewizcomputerscheduler.data.Computer
import doucette.marcus.codewizcomputerscheduler.data.DataService
import doucette.marcus.codewizcomputerscheduler.data.Student
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class NewEnrolmentState(
    val timeSlot:TimeSlot,
    val student: Student? = null,
    val allStudents:List<Student> = listOf(),
    val computer: FormattedComputer? = null,
    val relevantComputers:List<FormattedComputer> = listOf(),
    val currentClass: CWClass? = null,
    val allClasses: List<CWClass> = listOf(),
)


enum class ComputerAvailability {
    Free,
    Taken,
    Adjacent
}

data class FormattedComputer(
    val computerId:UUID,
    val name:String,
    val availability:ComputerAvailability
){
    companion object{
        fun from(newComputer: Computer):FormattedComputer{
            return FormattedComputer(newComputer.id,newComputer.name,ComputerAvailability.Free)
        }
    }
}


class NewEnrolmentViewModelFactory(private val timeSlotId: UUID, private val closePopup:()->Unit): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NewEnrolmentViewModel(timeSlotId,closePopup) as T
}

sealed interface NewEnrolmentAction{
    data class ChangeStudent(val newStudent:Student):NewEnrolmentAction
    data class ChangeComputer(val newComputer: FormattedComputer):NewEnrolmentAction
    data class ChangeClass(val newClass:CWClass):NewEnrolmentAction
    data object Submit:NewEnrolmentAction
    data object Cancel:NewEnrolmentAction
    data class CreateStudent(val studentName:String):NewEnrolmentAction
    data class CreateComputer(val computerName:String):NewEnrolmentAction
    data class CreateClass(val subjectName:String):NewEnrolmentAction
}


class NewEnrolmentViewModel(private var timeSlotId:UUID, private var cancelAction:()->Unit): ViewModel(){

    private val _state = MutableStateFlow(NewEnrolmentState(timeSlot = TimeSlot.NONE))
    val state = _state.asStateFlow()

    init{
        reaffirm(timeSlotId,cancelAction)
    }

    fun reaffirm(id:UUID, newAction:()->Unit){
        timeSlotId = id
        cancelAction = newAction

        viewModelScope.launch(Dispatchers.IO){
            _state.update {old->
                old.copy(
                    timeSlot = DataService.get().getTimeSlot(timeSlotId),
                    allStudents = DataService.get().getAllStudentsNotAlreadyEnrolledInTimeSlot(timeSlotId),
                    allClasses = DataService.get().getClassesForTimeSlot(timeSlotId),
                    relevantComputers = DataService.get().getFormattedComputers(timeSlotId),
                    currentClass = null,
                    student = null,
                    computer = null
                )
            }
        }
    }


    fun ActionHandler(action:NewEnrolmentAction){
        when(action){
            NewEnrolmentAction.Cancel -> {
                cancelAction()
            }
            is NewEnrolmentAction.ChangeClass -> {
                _state.update {
                    it.copy(
                        currentClass = action.newClass
                    )
                }
            }
            is NewEnrolmentAction.ChangeComputer ->{
                _state.update {
                    it.copy(
                        computer = action.newComputer
                    )
                }
            }
            is NewEnrolmentAction.ChangeStudent -> {
                _state.update{
                    it.copy(
                        student = action.newStudent
                    )
                }
            }
            NewEnrolmentAction.Submit -> {
                viewModelScope.launch(Dispatchers.IO){
                    val st = state.value.student?:run{ return@launch }
                    val cmp = state.value.computer?:run{ return@launch }
                    val cwc = state.value.currentClass?:run{ return@launch }
                    DataService.get().addEnrollment(st.id,cmp.computerId,cwc.id)
                    cancelAction()
                }

            }
            is NewEnrolmentAction.CreateClass -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val newClass = DataService.get().createClass(_state.value.timeSlot.id,action.subjectName)
                    _state.update {old->
                        old.copy(
                            allClasses = DataService.get().getClassesForTimeSlot(_state.value.timeSlot.id),
                            currentClass = newClass
                        )
                    }
                }
            }
            is NewEnrolmentAction.CreateComputer -> {
                viewModelScope.launch(Dispatchers.IO){
                    val newComputer:Computer = DataService.get().createComputer(action.computerName)
                    _state.update{old->
                        old.copy(
                            relevantComputers = DataService.get().getFormattedComputers(_state.value.timeSlot.id),
                            computer=FormattedComputer.from(newComputer)
                        )
                    }
                }
            }
            is NewEnrolmentAction.CreateStudent -> {
                viewModelScope.launch(Dispatchers.IO){
                    val newStudent = DataService.get().createStudent(action.studentName)
                    _state.update{old->
                        old.copy(
                            allStudents = DataService.get().getAllStudentsNotAlreadyEnrolledInTimeSlot(state.value.timeSlot.id),
                            student = newStudent
                        )
                    }
                }
            }
        }
    }

}