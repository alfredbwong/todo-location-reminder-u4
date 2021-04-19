package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.MainCoroutineRule
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var testDataSource: FakeDataSource
    private lateinit var testViewModel: SaveReminderViewModel

    @Test
    fun validateSaveReminder_saveSuccessful() {
        val reminderToSave = ReminderDataItem("R1", "R1-desc", "R1-location", 100.0, 100.0)
        testDataSource = FakeDataSource()
        testViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        runBlockingTest {
            testViewModel.validateAndSaveReminder(reminderToSave)
        }
        assertThat(testViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(testViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))

    }
    @Test
    fun validateSaveReminder_checkLoading() {
        val reminderToSave = ReminderDataItem("R1", "R1-desc", "R1-location", 100.0, 100.0)
        testDataSource = FakeDataSource()
        testViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        mainCoroutineRule.pauseDispatcher()
        runBlockingTest {
            testViewModel.validateAndSaveReminder(reminderToSave)
        }
        assertThat(testViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(testViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    @Test
    fun validateSaveReminder_shouldShowError_missingTitle() {
        val reminderToSave = ReminderDataItem("", "R1-desc", "R1-location", 100.0, 100.0)
        testDataSource = FakeDataSource()
        testViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        runBlockingTest {
            testViewModel.validateAndSaveReminder(reminderToSave)
        }
        assertThat(testViewModel.showSnackBarInt.getOrAwaitValue(), `is`(2131820601))


    }

    @Test
    fun validateSaveReminder_shouldShowError_nullTitle() {
        val reminderToSave = ReminderDataItem(null, "R1-desc", "R1-location", 100.0, 100.0)
        testDataSource = FakeDataSource()
        testViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        runBlockingTest {
            testViewModel.validateAndSaveReminder(reminderToSave)
        }
        assertThat(testViewModel.showSnackBarInt.getOrAwaitValue(), `is`(2131820601))
    }
    @Test
    fun validateSaveReminder_shouldShowError_missingLocation() {
        val reminderToSave = ReminderDataItem("R1", "R1-desc", "", 100.0, 100.0)
        testDataSource = FakeDataSource()
        testViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        runBlockingTest {
            testViewModel.validateAndSaveReminder(reminderToSave)
        }
        assertThat(testViewModel.showSnackBarInt.getOrAwaitValue(), `is`(2131820602))
    }
    @Test
    fun validateSaveReminder_shouldShowError_nullLocation() {
        val reminderToSave = ReminderDataItem("R1", "R1-desc", null, 100.0, 100.0)
        testDataSource = FakeDataSource()
        testViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        runBlockingTest {
            testViewModel.validateAndSaveReminder(reminderToSave)
        }
        assertThat(testViewModel.showSnackBarInt.getOrAwaitValue(), `is`(2131820602))
    }
    @After
    fun tearDown() {
        stopKoin()
    }
}