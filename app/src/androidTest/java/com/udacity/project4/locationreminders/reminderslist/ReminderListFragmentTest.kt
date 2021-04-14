package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.RecyclerViewMatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest :AutoCloseKoinTest() {

    private lateinit var testDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun setupWork(){
        stopKoin()
        testDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), testDataSource)
        val myModule = module {
            single {
                reminderListViewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun showReminderList() = runBlockingTest{
        val listOfReminders = mutableListOf<ReminderDTO>(
                ReminderDTO("R1", "R1-desc", "R1-location", 100.0, 100.0, "R1ID")
        )
        for (reminder in listOfReminders){
            testDataSource.saveReminder(reminder)
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))

        onView(RecyclerViewMatcher(R.id.reminderssRecyclerView)
                .atPositionOnView(0, R.id.reminderCardView) as Matcher<View>?)
                .check(matches(isDisplayed()))

        onView(withText("R1"))
            .check(matches(isDisplayed()))
        onView(withText("R1-desc"))
            .check(matches(isDisplayed()))
        onView(withText("R1-location"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showNoData() = runBlockingTest{
        val listOfReminders = mutableListOf<ReminderDTO>(
                //Empty
        )
        for (reminder in listOfReminders){
            testDataSource.saveReminder(reminder)
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches((isDisplayed())))


    }

    @Test
    fun navigateToAddReminder() = runBlockingTest {
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
    @Test
    fun errorScenario_nullDataSource() = runBlockingTest {
        testDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), testDataSource)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withText("Error getting list of reminders")).check(matches(isDisplayed()))
    }
}