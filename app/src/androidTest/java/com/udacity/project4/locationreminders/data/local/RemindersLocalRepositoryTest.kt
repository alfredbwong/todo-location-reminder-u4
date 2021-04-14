package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database :RemindersDatabase
    private lateinit var localDataSource: RemindersLocalRepository

    @Before
    fun setupWork(){
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        localDataSource = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

    }

    @Test
    fun getReminders_getExistingReminder() = runBlocking {
        //Given - A new reminder in the database
        val reminder = ReminderDTO("R1","R1-desc", "R1-location", 100.0, 100.0, "R1ID")
        localDataSource.saveReminder(reminder)

        //WHEN - getting the reminders
        val result = localDataSource.getReminders()

        //THEN - check same reminder is returned
        result as Result.Success
        val reminderFoundInDB = result.data[0]
        assertThat(reminderFoundInDB.title, `is` ("R1"))
        assertThat(reminderFoundInDB.description, `is` ("R1-desc"))
        assertThat(reminderFoundInDB.location, `is` ("R1-location"))
        assertThat(reminderFoundInDB.latitude, `is` (100.0))
        assertThat(reminderFoundInDB.longitude, `is` (100.0))

    }

    @Test
    fun saveAndGetReminder_saveTheReminder() = runBlocking {
        //Given - A new reminder to be save to DB
        val reminder = ReminderDTO("R1","R1-desc", "R1-location", 100.0, 100.0, "R1ID")

        //WHEN - save the reminders
        localDataSource.saveReminder(reminder)


        //THEN - check same reminder is returned
        val result = localDataSource.getReminder("R1ID")
        result as Result.Success
        val reminderFoundInDB = result.data
        assertThat(reminderFoundInDB.title, `is` ("R1"))
        assertThat(reminderFoundInDB.description, `is` ("R1-desc"))
        assertThat(reminderFoundInDB.location, `is` ("R1-location"))
        assertThat(reminderFoundInDB.latitude, `is` (100.0))
        assertThat(reminderFoundInDB.longitude, `is` (100.0))

    }

    @Test
    fun deleteReminder_saveReminderCheckThenRemove() = runBlocking {
        //Given - A new reminder to be save to DB
        val reminder = ReminderDTO("R1","R1-desc", "R1-location", 100.0, 100.0, "R1ID")
        localDataSource.saveReminder(reminder)
        val result = localDataSource.getReminders()
        result as Result.Success
        assertThat(result.data.size, `is`(1))

        //WHEN - delete the reminders
        localDataSource.deleteAllReminders()


        //THEN - check there is empty reminders
        val postResult = localDataSource.getReminders()
        postResult as Result.Success
        assertThat(postResult.data.size, `is`(0))


    }
    @Test
    fun getReminder_reminderDoesNotExist() = runBlocking {
        //WHEN- retreive a reminder that doesn't exist
        val message = (localDataSource.getReminder("R1FakeID") as? Result.Error)?.message

        //THEN - see an error log message
        assertThat(message, `is`("Reminder not found!"))
    }
    @After
    fun cleanUp() {
        database.close()
    }
}