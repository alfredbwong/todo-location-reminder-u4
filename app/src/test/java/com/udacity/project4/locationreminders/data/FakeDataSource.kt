package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (private var listOfReminders: LinkedHashMap<String, ReminderDTO> = linkedMapOf<String, ReminderDTO>()) : ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(listOfReminders.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        listOfReminders[reminder.id]= reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (listOfReminders.get(id) != null){
            return Result.Success(listOfReminders[id] as ReminderDTO)
        } else {
            return Result.Error("No reminder of the ID was found")

        }

    }

    override suspend fun deleteAllReminders() {
        listOfReminders = linkedMapOf<String,ReminderDTO>()
    }


}