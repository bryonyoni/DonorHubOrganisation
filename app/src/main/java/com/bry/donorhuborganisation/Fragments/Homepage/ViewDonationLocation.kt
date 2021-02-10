package com.bry.donorhuborganisation.Fragments.Homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bry.donorhuborganisation.Model.Donation
import com.bry.donorhuborganisation.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson

class ViewDonationLocation : Fragment() {
    var DONATION = "DONATION"
    private val ARG_DONATION = "ARG_DONATION"
    private lateinit var donation: Donation
    private lateinit var mMap: GoogleMap
    private var donation_list: ArrayList<Donation> = ArrayList()
    private var hasSetCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            donation = Gson().fromJson(it.getString(DONATION), Donation::class.java)
            donation_list = Gson().fromJson(it.getString(ARG_DONATION) as String, Donation.donation_list::class.java).donation_list
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        if(donation_list.isNotEmpty()){
            for(donation in donation_list){
                if (donation.location != null) {
                    if (donation.location.latitude != 0.0 && donation.location.longitude != 0.0) {
                        var pos = donation.location
//                    move_cam_to_location(pos, 17f)

                        googleMap.addMarker(
                            MarkerOptions()
                                .position(pos)
                                .title("donation.description")
                        )
                        if(!hasSetCamera){
                            hasSetCamera = true
                            move_cam_to_location(pos, 17f)
                        }

                    }
                }
            }

        }else{
            var pos = donation.location
            move_cam_to_location(pos, 17f)

            googleMap.addMarker(MarkerOptions()
                .position(pos)
                .title("donation.description"))
        }

    }

    fun move_cam_to_location(latLng: LatLng, zoom: Float){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val va = inflater.inflate(R.layout.fragment_view_donation_location, container, false)


        return va
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    companion object {

        @JvmStatic
        fun newInstance(donation: String, donations: String) = ViewDonationLocation().apply {
                    arguments = Bundle().apply {
                        putString(DONATION, donation)
                        putString(ARG_DONATION,donations)
                    }
                }
    }
}