package doucette.marcus.codewizcomputerscheduler.ui.ComputerSelectorScreen

sealed interface ComputerSelectorScreenAction{
    data class SelectComputer(val index:Int): ComputerSelectorScreenAction
    data object AddNewComputer: ComputerSelectorScreenAction
}