package com.fitflow.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "daily_stats") data class DailyStat(@PrimaryKey val date: String, val steps: Int = 0, val waterMl: Int = 0, val sleepMinutes: Int = 0, val calories: Int = 0)
@Entity(tableName = "habits") data class Habit(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String, val streak: Int = 0, val completedToday: Boolean = false)
@Entity(tableName = "settings") data class UserSettings(@PrimaryKey val id: Int = 1, val name: String = "Guest", val theme: String = "Orange", val language: String = "English", val stepsGoal: Int = 15000, val waterGoal: Int = 2500, val sleepGoal: Int = 480)
@Dao interface FitDao { @Query("SELECT * FROM settings WHERE id = 1") fun settings(): Flow<UserSettings?>; @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveSettings(settings: UserSettings); @Query("SELECT * FROM habits") fun habits(): Flow<List<Habit>>; @Insert suspend fun addHabit(habit: Habit) }
@Database(entities = [DailyStat::class, Habit::class, UserSettings::class], version = 1, exportSchema = false) abstract class FitDatabase : RoomDatabase() { abstract fun dao(): FitDao; companion object { fun create(context: Context) = Room.databaseBuilder(context, FitDatabase::class.java, "fitflow.db").fallbackToDestructiveMigration().build() } }
class FitRepository(private val dao: FitDao) { val settings = dao.settings(); val habits = dao.habits(); suspend fun save(settings: UserSettings) = dao.saveSettings(settings); suspend fun addHabit(name: String) = dao.addHabit(Habit(name = name)) }
