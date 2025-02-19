package doucette.marcus.codewizcomputerscheduler.ComputerSelectorScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ComputerSelectorScreenViewModel: ViewModel() {

    private val _state = MutableStateFlow(ComputerSelectorScreenState())
    val state = _state.asStateFlow()

    fun ActionHandler(action:ComputerSelectorScreenAction){

    }

}