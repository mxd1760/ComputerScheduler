package doucette.marcus.codewizcomputerscheduler.ComputerSelectorScreen

sealed interface ComputerSelectorScreenAction{
    data class SelectComputer(val index:Int): ComputerSelectorScreenAction
    data object AddNewComputer:ComputerSelectorScreenAction
}