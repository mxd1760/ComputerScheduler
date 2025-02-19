package doucette.marcus.codewizcomputerscheduler.ComputerSelectorScreen

data class ComputerEntryData(
    val name:String,
    val num_users:Int,
    val is_free_now:Boolean,
)

data class ComputerSelectorScreenState (
    val list: List<ComputerEntryData> = listOf()
)