package com.udacity.project4.data

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource (private var listOfReminders: LinkedHashMap<String, ReminderDTO>? = linkedMapOf()) :
    ReminderDataSource {

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        return if (listOfReminders != null) Result.Success(listOfReminders!!.values.toList())
        else Result.Error("Error getting list of reminders")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        listOfReminders?.set(reminder.id, reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (listOfReminders?.get(id) != null){
            return Result.Success(listOfReminders!![id] as ReminderDTO)
        } else {
            return Result.Error("No reminder of the ID was found")

        }

    }

    override suspend fun deleteAllReminders() {
        listOfReminders = linkedMapOf<String,ReminderDTO>()
    }


}