package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient

    companion object{
        const val TAG = "SaveReminderFragment"
        const val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = 10000
        const val GEOFENCE_DEFAULT_RADIUS = 100f

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("SaveReminderFragment", "create view for save reminder ")
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("SaveReminderFragment", "onViewCreated save reminder ")
        geofencingClient = LocationServices.getGeofencingClient(requireActivity().applicationContext)

        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude
            val longitude = _viewModel.longitude.value

            val reminderToSave = ReminderDataItem(
                    title,
                    description.value,
                    location,
                    latitude.value,
                    longitude
            )
            Log.i("SaveReminderFragment", "Save this marker $title, $latitude.value $longitude")

            _viewModel.validateAndSaveReminder(reminderToSave)

            if (latitude != null &&
                   longitude != null
                    && !TextUtils.isEmpty(title)){
                addGeofenceRequest(reminderToSave.id, reminderToSave.latitude, reminderToSave.longitude!!, GEOFENCE_DEFAULT_RADIUS)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
    @SuppressLint("MissingPermission")
    private fun addGeofenceRequest(
            geofenceId: String,
            latitude: Double?,
            longitude: Double,
            radius: Float
            ) {
        val geofence: Geofence = Geofence.Builder()
                .setCircularRegion(latitude!!, longitude, radius)
                .setRequestId(geofenceId)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(5000)
                .build()
        val geofencingRequest: GeofencingRequest = GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build()

        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(OnSuccessListener<Void?> {
                    Log.d(TAG, "Geofence Added Successfully")
                })
                .addOnFailureListener(OnFailureListener { err ->
                    Toast.makeText(context, "Some error occurred, location permission granted?", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Failed to create geofence: ${err.message}")
                })
    }
}
