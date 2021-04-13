package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var testDataSource: FakeDataSource
    private lateinit var testViewModel : RemindersListViewModel

    @Config(sdk = [Build.VERSION_CODES.O_MR1])
    @Test
    fun loadReminders_getRemindersList() {
        //Given a fresh viewmodel and loaded data
        val reminder1 =ReminderDTO("R1", "R1", "R1-location", 100.0, 100.0, "R1ID" )
        val reminder2 = ReminderDTO("R2", "R2", "R2-location", 200.0, 200.0, "R2ID" )
        val reminder3 = ReminderDTO("R3", "R3", "R3-location", 300.0, 300.0, "R3ID" )

        val reminderMapList = linkedMapOf<String, ReminderDTO>( Pair("R1", reminder1), Pair("R2", reminder2), Pair("R3", reminder3))
        testDataSource = FakeDataSource(reminderMapList)
        val testViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), testDataSource)

        //When load reminder
        testViewModel.loadReminders()

        //Then get reminders
        assertThat( testViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
        assertThat( testViewModel.remindersList.getOrAwaitValue().size, `is`(reminderMapList.size))
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}