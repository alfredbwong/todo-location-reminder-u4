package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.Assert.assertNull
import kotlinx.coroutines.Dispatchers

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database :RemindersDatabase

    @Before
    fun setupWork(){
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

    }

    @Test
    fun getReminders_getExistingReminder() = runBlockingTest {
        //Given - A new reminder in the database
        val reminder = ReminderDTO("R1","R1-desc", "R1-location", 100.0, 100.0, "R1ID")
        database.reminderDao().saveReminder(reminder)

        //WHEN - getting the reminders
        val reminderFoundInDB = database.reminderDao().getReminders()[0]

        //THEN - check same reminder is returned
        assertThat<ReminderDTO>(reminderFoundInDB as ReminderDTO, notNullValue())
        ViewMatchers.assertThat(reminderFoundInDB.title, Matchers.`is` ("R1"))
        ViewMatchers.assertThat(reminderFoundInDB.description, Matchers.`is` ("R1-desc"))
        ViewMatchers.assertThat(reminderFoundInDB.location, Matchers.`is` ("R1-location"))
        ViewMatchers.assertThat(reminderFoundInDB.latitude, Matchers.`is` (100.0))
        ViewMatchers.assertThat(reminderFoundInDB.longitude, Matchers.`is` (100.0))

    }

    @Test
    fun saveAndGetReminder_saveTheReminder() = runBlockingTest {
        //Given - A new reminder to be save to DB
        val reminder = ReminderDTO("R1","R1-desc", "R1-location", 100.0, 100.0, "R1ID")

        //WHEN - save the reminders
        database.reminderDao().saveReminder(reminder)


        //THEN - check same reminder is returned
        val reminderFoundInDB = database.reminderDao().getReminderById("R1ID")
        assertThat<ReminderDTO>(reminderFoundInDB as ReminderDTO, notNullValue())
        ViewMatchers.assertThat(reminderFoundInDB.title, Matchers.`is` ("R1"))
        ViewMatchers.assertThat(reminderFoundInDB.description, Matchers.`is` ("R1-desc"))
        ViewMatchers.assertThat(reminderFoundInDB.location, Matchers.`is` ("R1-location"))
        ViewMatchers.assertThat(reminderFoundInDB.latitude, Matchers.`is` (100.0))
        ViewMatchers.assertThat(reminderFoundInDB.longitude, Matchers.`is` (100.0))

    }

    @Test
    fun deleteReminder_saveReminderCheckThenRemove() = runBlockingTest {
        //Given - A new reminder to be save to DB
        val reminder = ReminderDTO("R1","R1-desc", "R1-location", 100.0, 100.0, "R1ID")
        database.reminderDao().saveReminder(reminder)
        val result = database.reminderDao().getReminders()
        ViewMatchers.assertThat(result.size, Matchers.`is`(1))

        //WHEN - delete the reminders
        database.reminderDao().deleteAllReminders()


        //THEN - check there is empty reminders
        val postResult = database.reminderDao().getReminders()
        ViewMatchers.assertThat(postResult.size, Matchers.`is`(0))


    }
    @Test
    fun getReminder_reminderDoesNotExist() = runBlockingTest {
        //WHEN- retreive a reminder that doesn't exist
        val result = database.reminderDao().getReminderById("R1FakeID")

        //THEN - check that it is null result
        assertNull(result)
    }
    @After
    fun cleanUp() {
        database.close()
    }

}