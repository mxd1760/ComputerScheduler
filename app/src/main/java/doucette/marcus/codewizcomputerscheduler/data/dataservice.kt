package doucette.marcus.codewizcomputerscheduler.data

import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RenameTable
import androidx.room.Room
import androidx.room.RoomDatabase
import doucette.marcus.codewizcomputerscheduler.MainActivity
import doucette.marcus.codewizcomputerscheduler.ui.NewEnrollmentPopup.ComputerAvailability
import doucette.marcus.codewizcomputerscheduler.ui.NewEnrollmentPopup.FormattedComputer
import doucette.marcus.codewizcomputerscheduler.ui.TimeSlotView.TimeSlotViewData
import java.time.DayOfWeek
import java.util.UUID


@Entity(tableName="time_slot")//, indices = [ @Index("time_and_day"),@Index(value={"day","time"}) ])
data class TimeSlot(
    @PrimaryKey val id:UUID,
    val day: DayOfWeek,
    val timeOfDay: Byte,
){
    companion object{
        val NONE = TimeSlot(UUID.nameUUIDFromBytes("NONE".toByteArray()),DayOfWeek.MONDAY, 0)
    }
    fun LabelString():String{
        return "${day.toString().take(3)} ${timeOfDay}:00"
    }
}

@Entity(tableName="cw_class")
data class CWClass( // recurring class
    @PrimaryKey val id:UUID,
    val timeSlotId:UUID,
    val subject:String,
    val notes:String = "",
    // TEACHER??
)


@Entity(tableName="enrollment", primaryKeys = ["studentId","classId"])
data class Enrolment(
    val studentId:UUID,
    val classId:UUID,
    val computerId:UUID,
)

@Entity(tableName = "student")
data class Student(
    @PrimaryKey val id:UUID,
    val name:String,
)

@Entity(tableName="computer")
data class Computer(
    @PrimaryKey val id:UUID,
    val name:String,
    val notes:String = "",
    val flags:Int = 0,
)


data class StudentCard(
    val name:String,
    val subject:String,
    val computer:String,
)

@Dao
interface CCSDAO{
    @Query("SELECT * FROM computer")
    fun getAllComputers():List<Computer>

    @Query("SELECT * FROM student")
    fun getAllStudents():List<Student>

    @Query("SELECT * FROM cw_class")
    fun getAllClasses():List<CWClass>

    @Query("SELECT * FROM time_slot")
    fun getAllTimeSlots():List<TimeSlot>

    @Query("SELECT * FROM enrollment")
    fun getAllEnrolments():List<Enrolment>

    @Insert
    fun insertComputer(comp:Computer)

    @Insert
    fun insertStudent(student:Student)

    @Insert
    fun insertClass(c:CWClass)

    @Insert
    fun insertTimeSlot(ts:TimeSlot)

    @Insert
    fun insertEnrolment(enr:Enrolment)

    @Query("SELECT * FROM time_slot WHERE id=:id")
    fun getTimeSlotById(id:UUID):TimeSlot?

    @Query("SELECT * from time_slot WHERE day=:day AND timeOfDay=:time")
    fun getTimeSlotByValues(day: DayOfWeek,time:Byte):TimeSlot?

    @Query("SELECT student.name AS name, cw_class.subject AS subject, computer.name AS computer " +
            "FROM student " +
            "JOIN enrollment ON enrollment.studentId = student.id " +
            "JOIN cw_class ON cw_class.id=enrollment.classId AND cw_class.TimeSlotId=:tsid " +
            "JOIN computer ON computer.id=enrollment.computerId;")
    fun getEnrollmentsAtTimeSlot(tsid:UUID):List<StudentCard>

    @Query("SELECT computer.id, computer.name,computer.notes, computer.flags FROM computer " +
            "INNER JOIN cw_class,time_slot,enrollment " +
            "ON time_slot.day=:day AND time_slot.timeOfDay=:time " +
            "AND cw_class.timeSlotId=time_slot.id AND enrollment.classId=cw_class.id " +
            "AND enrollment.computerId=computer.id; ")
    fun getComputersInTimeSlot(day:DayOfWeek,time:Byte):List<Computer>

    @Query("SELECT cw_class.id, cw_class.timeSlotId,cw_class.subject, cw_class.notes FROM cw_class WHERE cw_class.timeSlotId=:tsid")
    fun getClassesInTimeSlot(tsid:UUID):List<CWClass>

    @Query("SELECT student.* FROM student " +
//            "LEFT JOIN enrollment ON enrollment.studentId=studentId " +
//            "LEFT JOIN cw_class ON cw_class.id=enrollment.studentId AND cw_class.timeSlotId=:timeSlotId " +
//            "WHERE enrollment.studentId IS NULL OR cw_class.id IS NULL;")
            "WHERE student.id NOT IN " +
            "(SELECT enrollment.studentId FROM enrollment " +
            "INNER JOIN cw_class WHERE " +
            "enrollment.classId=cw_class.id AND " +
            "cw_class.timeSlotId=:timeSlotId); ")
    fun getAllStudentsNotAtTimeslot(timeSlotId: UUID):List<Student>

    @Query("DELETE FROM enrollment WHERE enrollment.classId IN "+
            "(SELECT cw_class.id FROM cw_class " +
            "WHERE cw_class.timeSlotId=:tsid); ")
    fun deleteEnrolmentsByTSID(tsid:UUID)

    @Query("DELETE FROM cw_class WHERE cw_class.timeSlotId=:tsid;")
    fun deleteClassByTSID(tsid:UUID)

    @Query("DELETE FROM time_slot WHERE time_slot.id=:tsid")
    fun deleteTimeSlotById(tsid:UUID)
}

@Database(entities=arrayOf(Computer::class,Student::class,CWClass::class,Enrolment::class,TimeSlot::class),version=2)
abstract class AppDatabase:RoomDatabase(){
    abstract fun dao():CCSDAO
}



class DataService() {
    private val dao:CCSDAO
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
        val db = Room.databaseBuilder(MainActivity.getAppContext(),AppDatabase::class.java,"ccs-db")
            .fallbackToDestructiveMigration(true).build()
        dao = db.dao();

    }
    fun getTimeSlotViewData():List<TimeSlotViewData>{
        val data = dao.getAllTimeSlots().map{timeSlot->
            TimeSlotViewData(
                timeSlot=timeSlot,
                students = dao.getEnrollmentsAtTimeSlot(timeSlot.id)
            )
        }

        return data
    }

    fun getTimeSlot(id:UUID):TimeSlot{
        return dao.getTimeSlotById(id)?:TimeSlot.NONE
    }

    fun createTimeSlot(day:DayOfWeek, time:Int){
        if (dao.getTimeSlotByValues(day,time.toByte())==null){
           dao.insertTimeSlot(TimeSlot(UUID.randomUUID(),day,time.toByte()))
        }
    }

    fun createClass(timeSlotId: UUID,subjectName:String):CWClass{
        val v = CWClass(UUID.randomUUID(),timeSlotId,subjectName,"")
        dao.insertClass(v)
        return v
    }

    fun getFormattedComputers(timeSlotId:UUID):List<FormattedComputer>{
        val ts = dao.getTimeSlotById(id = timeSlotId)?:run{
            return listOf()
        }
        val current = dao.getComputersInTimeSlot(ts.day,ts.timeOfDay)
        val adjacent = (dao.getComputersInTimeSlot(ts.day,(ts.timeOfDay+1).toByte()) + dao.getComputersInTimeSlot(ts.day,(ts.timeOfDay-1).toByte())).filter{
            !(current.contains(it))
        }
        val remaining = dao.getAllComputers().filter{
            !(current.contains(it) || adjacent.contains(it))
        }
        val out:MutableList<FormattedComputer> = mutableListOf()
        out.addAll(remaining.map{FormattedComputer(it.id,it.name,ComputerAvailability.Free) })
        out.addAll(adjacent.map{ FormattedComputer(it.id,it.name,ComputerAvailability.Adjacent) })
        out.addAll(current.map{ FormattedComputer(it.id,it.name,ComputerAvailability.Taken) })
        return out.toList()
    }

    fun getClassesForTimeSlot(timeSlotId:UUID):List<CWClass>{
        return dao.getClassesInTimeSlot(timeSlotId)
    }

    fun createComputer(name:String):Computer{
        val v = Computer(UUID.randomUUID(),name)
        dao.insertComputer(v)
        return v
    }

    fun createStudent(name:String):Student{
        val v = Student(UUID.randomUUID(),name)
        dao.insertStudent(v)
        return v
    }

    fun getAllStudents():List<Student>{
        return dao.getAllStudents()
    }

    fun getAllStudentsNotAlreadyEnrolledInTimeSlot(timeSlotId:UUID):List<Student>{
        return dao.getAllStudentsNotAtTimeslot(timeSlotId)
    }

    fun addEnrollment(studentId: UUID,computerId: UUID,classId: UUID){
        dao.insertEnrolment(Enrolment(studentId,classId,computerId))
    }

    fun deleteTimeSlot(tsid:UUID){
        dao.deleteEnrolmentsByTSID(tsid)
        dao.deleteClassByTSID(tsid)
        dao.deleteTimeSlotById(tsid)
    }

}

