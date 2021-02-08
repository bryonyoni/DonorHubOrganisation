package com.bry.donorhuborganisation.Fragments.Homepage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bry.donorhuborganisation.Constants
import com.bry.donorhuborganisation.Model.Donation
import com.bry.donorhuborganisation.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson


class ViewImage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_IMAGE = "ARG_IMAGE"
    private val ARG_DONATION = "ARG_DONATION"
    private lateinit var donation_image: Donation.donation_image
    private lateinit var donation: Donation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            donation_image = Gson().fromJson(it.getString(ARG_IMAGE) as String, Donation.donation_image::class.java)
            donation = Gson().fromJson(it.getString(ARG_DONATION) as String, Donation::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_view_image, container, false)
        val display_image : ImageView = va.findViewById(R.id.display_image)

        val storageReference: StorageReference = Firebase.storage.reference
            .child(Constants().donation_data)
            .child(donation.donation_id)
            .child(donation_image.name + ".jpg")
        Constants().load_normal_job_image(storageReference, display_image, context!!)

        return va
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, image: String, donation: String) =
            ViewImage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_IMAGE, image)
                    putString(ARG_DONATION,donation)
                }
            }
    }
}