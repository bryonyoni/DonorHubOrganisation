package com.bry.donorhuborganisation.Fragments.Homepage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bry.donorhuborganisation.Constants
import com.bry.donorhuborganisation.Model.Donation
import com.bry.donorhuborganisation.Model.Organisation
import com.bry.donorhuborganisation.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList

class ViewDonation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_ORGANISATION = "ARG_ORGANISATION"
    private val ARG_DONATION = "ARG_DONATION"
    private val ARG_ACTIVITIES = "ARG_ACTIVITIES"
    private val ARG_USER = "ARG_USER"
    private lateinit var organisation: Organisation
    private lateinit var donation: Donation
    private lateinit var user: Constants.user
    private lateinit var activities: ArrayList<Donation.activity>
    private lateinit var listener: ViewDonationInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            organisation = Gson().fromJson(it.getString(ARG_ORGANISATION), Organisation::class.java)
            donation = Gson().fromJson(it.getString(ARG_DONATION) as String, Donation::class.java)
            activities = Gson().fromJson(it.getString(ARG_ACTIVITIES), Donation.activities::class.java).activities
            user = Gson().fromJson(it.getString(ARG_USER) as String, Constants.user::class.java)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ViewDonationInterface){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_view_donation, container, false)
        val donation_desc: TextView = va.findViewById(R.id.donation_desc)
        val donation_time: TextView = va.findViewById(R.id.donation_time)
        val donation_images_recyclerview: RecyclerView = va.findViewById(R.id.donation_images_recyclerview)
        val schedule_relative: RelativeLayout = va.findViewById(R.id.schedule_relative)
        val finish_relative: RelativeLayout = va.findViewById(R.id.finish_relative)
        val share_location_layout: LinearLayout = va.findViewById(R.id.share_location_layout)
        val share_location_switch: Switch = va.findViewById(R.id.share_location_switch)
        val activities_recyclerview: RecyclerView = va.findViewById(R.id.activities_recyclerview)

        val uploader_textview: TextView = va.findViewById(R.id.uploader_textview)
        val uploader_number_textview: TextView = va.findViewById(R.id.uploader_number_textview)
        val uploader_email_textview: TextView = va.findViewById(R.id.uploader_email_textview)

        donation_desc.text = donation.description
        val age = Constants().construct_elapsed_time(Calendar.getInstance().timeInMillis - donation.creation_time)
        donation_time.text = "Request sent $age ago."

        if(activities.isNotEmpty()){
            activities_recyclerview.adapter = ActivitiesListAdapter()
            activities_recyclerview.layoutManager = LinearLayoutManager(context)
        }

        donation_images_recyclerview.adapter = ImageListAdapter(donation)
        donation_images_recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,
            false)

        schedule_relative.setOnClickListener {
            listener.whenScheduleDonationPickupClicked(donation,organisation)
        }

        finish_relative.setOnClickListener {
            listener.whenFinishDonationPickupClicked(donation,organisation)
        }

        share_location_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            listener.whenFinishDonationShareLocation(donation,organisation,isChecked)
        }

        uploader_textview.text = user.name
        uploader_number_textview.text = "${user.phone.country_number_code} ${user.phone.digit_number}"
        uploader_email_textview.text = user.email

        return va
    }

    internal inner class ImageListAdapter(var donation: Donation) : RecyclerView.Adapter<ViewHolderImages>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderImages {
            val vh = ViewHolderImages(LayoutInflater.from(context)
                .inflate(R.layout.recycler_item_donation_image, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(viewHolder: ViewHolderImages, position: Int) {
            val image = donation.images[position]

            val storageReference: StorageReference = Firebase.storage.reference
                .child(Constants().donation_data)
                .child(donation.donation_id)
                .child(image.name + ".jpg")
            Constants().load_round_job_image(storageReference, viewHolder.image_view, context!!)

            viewHolder.image_view.setOnClickListener {
                listener.whenViewDonationImage(image, donation)
            }

        }

        override fun getItemCount() = donation.images.size


    }

    internal inner class ViewHolderImages (view: View) : RecyclerView.ViewHolder(view) {
        val image_view: ImageView = view.findViewById(R.id.image)
    }


    internal inner class ActivitiesListAdapter : RecyclerView.Adapter<ViewHolderActivities>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderActivities {
            val vh = ViewHolderActivities(LayoutInflater.from(context).inflate(R.layout.recycler_item_activities, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(v: ViewHolderActivities, position: Int) {
            val activity = activities[position]

            v.activity_explanation.text = activity.explanation
            v.activity_time.text = Constants().construct_elapsed_time(Calendar.getInstance().timeInMillis - activity.time)
        }

        override fun getItemCount() = activities.size

    }

    internal inner class ViewHolderActivities (view: View) : RecyclerView.ViewHolder(view) {
        val activity_explanation: TextView = view.findViewById(R.id.activity_explanation)
        val activity_time: TextView = view.findViewById(R.id.activity_time)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, organisation: String, donation: String, activities: String, user: String) =
            ViewDonation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_ORGANISATION,organisation)
                    putString(ARG_DONATION,donation)
                    putString(ARG_ACTIVITIES, activities)
                    putString(ARG_USER, user)
                }
            }
    }

    interface ViewDonationInterface{
        fun whenScheduleDonationPickupClicked(donation: Donation, organisation: Organisation)
        fun whenFinishDonationPickupClicked(donation: Donation, organisation: Organisation)
        fun whenFinishDonationShareLocation(donation: Donation, organisation: Organisation, isToShareLoc: Boolean)
        fun whenViewDonationImage(image: Donation.donation_image, donation: Donation)
    }
}