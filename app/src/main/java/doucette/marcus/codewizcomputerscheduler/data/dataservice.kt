package doucette.marcus.codewizcomputerscheduler.data

import android.util.Log
import doucette.marcus.codewizcomputerscheduler.MainActivity
import doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView.StudentCard
import doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView.TimeSlotViewData
import java.sql.Time
import java.time.DayOfWeek
import java.util.UUID


data class TimeSlot(
    val day: DayOfWeek,
    val timeOfDay: Byte,
    val enrolments: MutableList<Enrolment>,
    val classes: List<UUID>,
)

data class CWClass( // recurring class
    val id:UUID,
    val subject:String,
    val notes:String,
    // TEACHER??
)

// class sessions???


data class Enrolment(
    val studentId:UUID,
    val classId:UUID,
    val computerId:UUID,
)

data class Student(
    val id:UUID,
    val name:String,
)

data class Computer(
    val id:UUID,
    val name:String,
    val notes:String,
    val flags:Int,
)

class DataService() {
    private val timeSlots:MutableList<TimeSlot> = mutableListOf()
    private val classes:MutableMap<UUID,CWClass> = mutableMapOf()
    private val students:MutableMap<UUID,Student> = mutableMapOf()
    private val computers:MutableMap<UUID,Computer> = mutableMapOf()
    companion object{
        val NOT_NEEDED = UUID.nameUUIDFromBytes("not needed".toByteArray())
        val PERSONAL = UUID.nameUUIDFromBytes("personal".toByteArray())
        private var instance:DataService?=null
        fun get():DataService{
            if(instance==null){
                instance = DataService()
            }
            return instance as DataService
        }
    }

    init {
        loadMocks()
    }

    fun addEnrolments(enrolments:List<Enrolment>){
        timeSlots.forEach{timeSlot->
            timeSlot.enrolments.addAll(enrolments.filter{enrolment->
                timeSlot.classes.contains(enrolment.classId)
            })
        }
    }

    //TODO remove in release builds
    private fun loadMocks(){
        mockComputers()
        mockClasses()
        mockTimeSlots()
        mockStudentsAndEnrolments()
    }

    private fun mockComputers(){
        computers.clear()
        for(i in 0..30){
            val id = UUID.nameUUIDFromBytes(i.toString().toByteArray())
            computers.put(
                id,
                Computer(
                    id,
                    "center $i",
                    "",
                    0,
                )
            )
        }
    }

    private fun mockClasses(){
        classes.clear()
        val thuRoblox = CWClass(
            UUID.nameUUIDFromBytes("thuRoblox".toByteArray()),
            "Roblox",
            ""
        )
        val thuMC = CWClass(
            UUID.nameUUIDFromBytes("thuMC".toByteArray()),
            "MC Modding",
            ""
        )
        val thuGodot = CWClass(
            UUID.nameUUIDFromBytes("thuGodot".toByteArray()),
            "Godot",
            ""
        )
        val wedClass1 = CWClass(
            UUID.nameUUIDFromBytes("wedClass1".toByteArray()),
            "Scratch",
            ""
        )
        val wedMC = CWClass(
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            "MC Modding",
            ""
        )
        val wedRoblox = CWClass(
            UUID.nameUUIDFromBytes("wedRoblox".toByteArray()),
            "Roblox",
            ""
        )
        val wedGodot = CWClass(
            UUID.nameUUIDFromBytes("wedGodot".toByteArray()),
            "Godot",
            ""
        )
        val tueRoblox = CWClass(
            UUID.nameUUIDFromBytes("tueRoblox".toByteArray()),
            "Roblox",
            ""
        )
        classes.put(thuRoblox.id,thuRoblox)
        classes.put(thuMC.id,thuMC)
        classes.put(thuGodot.id,thuGodot)
        classes.put(wedClass1.id,wedClass1)
        classes.put(wedMC.id,wedMC)
        classes.put(wedRoblox.id,wedRoblox)
        classes.put(wedGodot.id,wedGodot)
        classes.put(tueRoblox.id,tueRoblox)
    }

    private fun mockTimeSlots(){
        timeSlots.clear()
        val ts1 = TimeSlot(
            day=DayOfWeek.WEDNESDAY,
            timeOfDay = 16,
            enrolments = mutableListOf(),
            classes = listOf(UUID.nameUUIDFromBytes("wedClass1".toByteArray()))
        )
        val ts2 = TimeSlot(
            day=DayOfWeek.WEDNESDAY,
            timeOfDay = 17,
            enrolments = mutableListOf(),
            classes = listOf(UUID.nameUUIDFromBytes("wedMC".toByteArray()))
        )
        val ts3 = TimeSlot(
            day=DayOfWeek.WEDNESDAY,
            timeOfDay=18,
            enrolments = mutableListOf(),
            classes = listOf(UUID.nameUUIDFromBytes("wedRoblox".toByteArray()),
                UUID.nameUUIDFromBytes("wedGodot".toByteArray()))
        )
        val ts4 = TimeSlot(
            day=DayOfWeek.THURSDAY,
            timeOfDay=16,
            enrolments=mutableListOf(),
            classes=listOf(UUID.nameUUIDFromBytes("thuRoblox".toByteArray()))
        )
        val ts5 = TimeSlot(
            day=DayOfWeek.THURSDAY,
            timeOfDay=17,
            enrolments = mutableListOf(),
            classes=listOf(UUID.nameUUIDFromBytes("thuMC".toByteArray()))
        )
        val ts6 = TimeSlot(
            day=DayOfWeek.THURSDAY,
            timeOfDay=18,
            enrolments=mutableListOf(),
            classes = listOf(UUID.nameUUIDFromBytes("thuGodot".toByteArray()))
        )
        val ts7 = TimeSlot(
            day=DayOfWeek.TUESDAY,
            timeOfDay=17,
            enrolments = mutableListOf(),
            classes=listOf(UUID.nameUUIDFromBytes("tueRoblox".toByteArray()))
        )
        timeSlots.add(ts1)
        timeSlots.add(ts2)
        timeSlots.add(ts3)
        timeSlots.add(ts4)
        timeSlots.add(ts5)
        timeSlots.add(ts6)
        timeSlots.add(ts7)
    }

    private fun mockStudentsAndEnrolments(){
        students.clear()
        //enrolments.clear()
        val student1 = Student(
            UUID.randomUUID(),
            "ana",
        )
        val enrolment1 = Enrolment(
            student1.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("15".toByteArray())
        )
        val student2 = Student(
            UUID.randomUUID(),
            "billy",
        )
        val enrolment2 = Enrolment(
            student2.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("10".toByteArray())
        )
        val student3 = Student(
            UUID.randomUUID(),
            "cassie",
        )
        val enrolment3 = Enrolment(
            student3.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("26".toByteArray())
        )
        val enrolment4 = Enrolment(
            student3.id,
            UUID.nameUUIDFromBytes("thuGodot".toByteArray()),
            UUID.nameUUIDFromBytes("2".toByteArray())
        )
        val student4 = Student(
            UUID.randomUUID(),
            "daisy",
        )
        val enrolment5 = Enrolment(
            student4.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("8".toByteArray())
        )
        val student5 = Student(
            UUID.randomUUID(),
            "eddie",
        )
        val enrolment6 = Enrolment(
            student5.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("6".toByteArray())
        )
        val student6 = Student(
            UUID.randomUUID(),
            "frank",
        )
        val enrolment7 = Enrolment(
            student6.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("24".toByteArray())
        )
        val student7 = Student(
            UUID.randomUUID(),
            "greg",
        )
        val enrolment8 = Enrolment(
            student7.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("7".toByteArray())
        )
        val student8 = Student(
            UUID.randomUUID(),
            "harry",
        )
        val enrolment9 = Enrolment(
            student8.id,
            UUID.nameUUIDFromBytes("wedRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("14".toByteArray())
        )
        val student9 = Student(
            UUID.randomUUID(),
            "issac",
        )
        val enrolment10 = Enrolment(
            student9.id,
            UUID.nameUUIDFromBytes("wedRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("9".toByteArray())
        )
        val student10 = Student(
            UUID.randomUUID(),
            "jerome",
        )
        val enrolment11 = Enrolment(
            student10.id,
            UUID.nameUUIDFromBytes("wedRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("5".toByteArray())
        )
        val student11 = Student(
            UUID.randomUUID(),
            "kenny",
        )
        val enrolment12=Enrolment(
            student11.id,
            UUID.nameUUIDFromBytes("wedMC".toByteArray()),
            UUID.nameUUIDFromBytes("16".toByteArray())
        )
        val student12 = Student(
            UUID.randomUUID(),
            "liam",
        )
        val enrolment13=Enrolment(
            student12.id,
            UUID.nameUUIDFromBytes("wedRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("17".toByteArray())
        )
        val student13 = Student(
            UUID.randomUUID(),
            "mike",
        )
        val enrolment14=Enrolment(
            student13.id,
            UUID.nameUUIDFromBytes("wedGodot".toByteArray()),
            UUID.nameUUIDFromBytes("4".toByteArray())
        )
        val enrolment15=Enrolment(
            student13.id,
            UUID.nameUUIDFromBytes("thuGodot".toByteArray()),
            UUID.nameUUIDFromBytes("15".toByteArray())
        )
        val student14 = Student(
            UUID.randomUUID(),
            "nick",
        )
        val enrolment16=Enrolment(
            student14.id,
            UUID.nameUUIDFromBytes("thuMC".toByteArray()),
            UUID.nameUUIDFromBytes("3".toByteArray())
        )
        val student15 = Student(
            UUID.randomUUID(),
            "oliver",
        )
        val enrolment17=Enrolment(
            student15.id,
            UUID.nameUUIDFromBytes("thuMC".toByteArray()),
            PERSONAL
        )
        val student16 = Student(
            UUID.randomUUID(),
            "patrick",
        )
        val enrolment18=Enrolment(
            student16.id,
            UUID.nameUUIDFromBytes("thuMC".toByteArray()),
            UUID.nameUUIDFromBytes("4".toByteArray())
        )
        val student17 = Student(
            UUID.randomUUID(),
            "quincy",
        )
        val enrolment19 = Enrolment(
            student17.id,
            UUID.nameUUIDFromBytes("thuGodot".toByteArray()),
            UUID.nameUUIDFromBytes("25".toByteArray())
        )
        val student18 = Student(
            UUID.randomUUID(),
            "remy",
        )
        val enrolment20=Enrolment(
            student18.id,
            UUID.nameUUIDFromBytes("thuRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("10".toByteArray())
        )
        val student19 = Student(
            UUID.randomUUID(),
            "stan",
        )
        val enrolment21=Enrolment(
            student19.id,
            UUID.nameUUIDFromBytes("thuRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("9".toByteArray())
        )
        val student20 = Student(
            UUID.randomUUID(),
            "teddy",
        )
        val enrolment22=Enrolment(
            student20.id,
            UUID.nameUUIDFromBytes("thuRoblox".toByteArray()),
            UUID.nameUUIDFromBytes("7".toByteArray())
        )
        students.put(student1.id,student1)
        students.put(student2.id,student2)
        students.put(student3.id,student3)
        students.put(student4.id,student4)
        students.put(student5.id,student5)
        students.put(student6.id,student6)
        students.put(student7.id,student7)
        students.put(student8.id,student8)
        students.put(student9.id,student9)
        students.put(student10.id,student10)
        students.put(student11.id,student11)
        students.put(student12.id,student12)
        students.put(student13.id,student13)
        students.put(student14.id,student14)
        students.put(student15.id,student15)
        students.put(student16.id,student16)
        students.put(student17.id,student17)
        students.put(student18.id,student18)
        students.put(student19.id,student19)
        students.put(student20.id,student20)
        val enrols = listOf(
            enrolment1, enrolment2, enrolment3, enrolment4, enrolment5,
            enrolment6, enrolment7, enrolment8, enrolment9, enrolment10,
            enrolment11, enrolment12, enrolment13, enrolment14, enrolment15,
            enrolment16, enrolment17, enrolment18, enrolment19, enrolment20,
            enrolment21, enrolment22
        )
        addEnrolments(enrols)
    }


    fun getTimeSlotViewData():List<TimeSlotViewData>{
        val data = timeSlots.toList().map{timeSlot->
            TimeSlotViewData(
                day = timeSlot.day,
                time = timeSlot.timeOfDay.toInt(),
                students = getStudentsFromEnrolments(timeSlot.enrolments)
            )
        }
        //Log.d(MainActivity.LOG_TAG, data.toString())
        return data
    }

    fun getStudentsFromEnrolments(enrolments: List<Enrolment>):List<StudentCard>{
        return enrolments.map{enrolment->
            StudentCard(
                name = students[enrolment.studentId]?.name?:"ERROR",
                subject = classes[enrolment.classId]?.subject?:"ERROR",
                computer = computers[enrolment.computerId]?.name?:"ERROR"
            )
        }
    }

    fun loadData(){

    }

    fun saveData(){

    }

    fun getTodaysClasses(){

    }

    fun getFreeComputersForClass(){

    }

}


