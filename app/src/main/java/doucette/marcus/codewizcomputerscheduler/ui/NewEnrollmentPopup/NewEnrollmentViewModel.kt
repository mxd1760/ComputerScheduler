package doucette.marcus.codewizcomputerscheduler.ui.NewEnrollmentPopup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import doucette.marcus.codewizcomputerscheduler.data.CWClass
import doucette.marcus.codewizcomputerscheduler.data.Computer
import doucette.marcus.codewizcomputerscheduler.data.DataService
import doucette.marcus.codewizcomputerscheduler.data.Enrolment
import doucette.marcus.codewizcomputerscheduler.data.Student
import doucette.marcus.codewizcomputerscheduler.data.TimeSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class NewEnrollmentState(
    val timeSlot:TimeSlot,
    val prev_enrollment: Enrolment?=null,
    val student: Student? = null,
    val computer: FormattedComputer? = null,
    val currentClass: CWClass? = null,
    val allStudents:List<Student> = listOf(),
    val relevantComputers:List<FormattedComputer> = listOf(),
    val allClasses: List<CWClass> = listOf(),
){
    companion object{
        fun from(timeSlot:TimeSlot,enrollment:Enrolment):NewEnrollmentState{
            val ds = DataService.get()
            val currentClass = ds.getClassFromId(enrollment.classId)
            val student = ds.getStudentFromId(enrollment.studentId)
            val computer = FormattedComputer.from(ds.getComputerFromId(enrollment.computerId))
            return NewEnrollmentState(timeSlot, enrollment, student,computer,currentClass,
                allStudents = DataService.get().getAllStudentsNotAlreadyEnrolledInTimeSlot(timeSlot.id),
                allClasses = DataService.get().getClassesForTimeSlot(timeSlot.id),
                relevantComputers = DataService.get().getFormattedComputers(timeSlot.id),
                )
        }
    }
}


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
        fun from(newComputer: Computer?):FormattedComputer?{
            return newComputer?.let{cmp->
                FormattedComputer(cmp.id,cmp.name,ComputerAvailability.Free)
            }
        }
    }
}


class NewEnrollmentViewModelFactory(private val timeSlotId: UUID, private val closePopup:()->Unit): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NewEnrollmentViewModel(timeSlotId,closePopup) as T
}
class EditEnrollmentViewModelFactory(val enrollment:Enrolment,val time_slot:TimeSlot,
                                     private val closePopup:()->Unit):ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass:Class<T>):T = NewEnrollmentViewModel(
        enrollment,time_slot,closePopup
    ) as T
}

sealed interface NewEnrollmentAction{
    data class ChangeStudent(val newStudent:Student):NewEnrollmentAction
    data class ChangeComputer(val newComputer: FormattedComputer):NewEnrollmentAction
    data class ChangeClass(val newClass:CWClass):NewEnrollmentAction
    data object Submit:NewEnrollmentAction
    data object Cancel:NewEnrollmentAction
    data class CreateStudent(val studentName:String):NewEnrollmentAction
    data class CreateComputer(val computerName:String):NewEnrollmentAction
    data class CreateClass(val subjectName:String):NewEnrollmentAction
}


class NewEnrollmentViewModel(): ViewModel(){

    constructor(id:UUID,action:()->Unit) : this() {
        timeSlotId=id
        cancelAction=action;
        reaffirm(timeSlotId,cancelAction)
    }
    constructor(enrollment: Enrolment,timeSlot:TimeSlot,action:()->Unit) : this() {
        viewModelScope.launch(Dispatchers.IO) {
            timeSlotId = timeSlot.id
            cancelAction = action
            _state.update{
                NewEnrollmentState.from(timeSlot,enrollment)
            }
        }
    }

    private lateinit var timeSlotId:UUID
    private lateinit var cancelAction:()->Unit

    private val _state = MutableStateFlow(NewEnrollmentState(timeSlot = TimeSlot.NONE))
    val state = _state.asStateFlow()

    fun reaffirm(id:UUID, newAction:()->Unit){
        timeSlotId = id
        cancelAction = newAction

        viewModelScope.launch(Dispatchers.IO){
            _state.update {old->
                old.copy(
                    timeSlot = DataService.get().getTimeSlotFromId(timeSlotId),
                    prev_enrollment = null,
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
    fun reaffirm(enrollment:Enrolment,timeSlot:TimeSlot,newAction:()->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            cancelAction= newAction
            timeSlotId = timeSlot.id
            _state.update{
                NewEnrollmentState.from(timeSlot,enrollment)
            }
        }
    }


    fun ActionHandler(action:NewEnrollmentAction){
        when(action){
            NewEnrollmentAction.Cancel -> {
                cancelAction()
            }
            is NewEnrollmentAction.ChangeClass -> {
                _state.update {
                    it.copy(
                        currentClass = action.newClass
                    )
                }
            }
            is NewEnrollmentAction.ChangeComputer ->{
                _state.update {
                    it.copy(
                        computer = action.newComputer
                    )
                }
            }
            is NewEnrollmentAction.ChangeStudent -> {
                _state.update{
                    it.copy(
                        student = action.newStudent
                    )
                }
            }
            NewEnrollmentAction.Submit -> {
                viewModelScope.launch(Dispatchers.IO){
                    val ds = DataService.get()
                    state.value.prev_enrollment?.let{pe->ds.deleteEnrollment(pe)}
                    val st = state.value.student?:run{ return@launch }
                    val cmp = state.value.computer?:run{ return@launch }
                    val cwc = state.value.currentClass?:run{ return@launch }
                    ds.addEnrollment(st.id,cmp.computerId,cwc.id)
                    cancelAction()
                }

            }
            is NewEnrollmentAction.CreateClass -> {
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
            is NewEnrollmentAction.CreateComputer -> {
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
            is NewEnrollmentAction.CreateStudent -> {
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