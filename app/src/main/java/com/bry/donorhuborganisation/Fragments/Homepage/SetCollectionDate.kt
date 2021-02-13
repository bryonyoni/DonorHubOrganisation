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
import com.bry.donorhuborganisation.Models.Collectors
import com.bry.donorhuborganisation.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class SetCollectionDate : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_COLLECTORS = "ARG_COLLECTORS"
    private val ARG_ORGANISATION = "ARG_ORGANISATION"
    private val ARG_DONATION = "ARG_DONATION"
    private lateinit var organisation: Organisation
    private lateinit var donation: Donation
    private var collectors: ArrayList<Collectors> = ArrayList()
    private lateinit var listener: SetCollectionDateInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            organisation = Gson().fromJson(it.getString(ARG_ORGANISATION), Organisation::class.java)
            donation = Gson().fromJson(it.getString(ARG_DONATION) as String, Donation::class.java)
            if(!it.getString(ARG_COLLECTORS).equals("")){
                collectors = Gson().fromJson(it.getString(ARG_COLLECTORS) as String,
                    Collectors.Collector_list::class.java).collector_list
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is SetCollectionDateInterface){
            listener = context
        }
    }


    var whenUserPicked: (collector: Collectors) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_set_collection_date, container, false)
        val datePicker: DatePicker = va.findViewById(R.id.datePicker)
        val donation_desc: TextView = va.findViewById(R.id.donation_desc)
        val donation_time: TextView = va.findViewById(R.id.donation_time)
        val donation_images_recyclerview: RecyclerView = va.findViewById(R.id.donation_images_recyclerview)
        val people_recyclerview: RecyclerView = va.findViewById(R.id.people_recyclerview)

        donation_desc.text = donation.description
        val age = Constants().construct_elapsed_time(Calendar.getInstance().timeInMillis - donation.creation_time)
        donation_time.text = "Request sent $age ago."

        donation_images_recyclerview.adapter = ImageListAdapter(donation)
        donation_images_recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        people_recyclerview.adapter = CollectorsListAdapter()
        people_recyclerview.layoutManager = LinearLayoutManager(context)

        whenUserPicked = {
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth

            Toast.makeText(context, "set for ${day}: ${month+1}: ${year}", Toast.LENGTH_SHORT).show()
            val ca = Calendar.getInstance()
            ca.set(Calendar.YEAR, year)
            ca.set(Calendar.MONTH, month)
            ca.set(Calendar.DAY_OF_MONTH, day)

            val time_in_mills = ca.timeInMillis

            listener.whenSetCollectionDateCollectorPicked(it,donation,organisation,time_in_mills)
        }

        return va
    }


    internal inner class CollectorsListAdapter : RecyclerView.Adapter<ViewHolderUsers>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderUsers {
            val vh = ViewHolderUsers(LayoutInflater.from(context)
                .inflate(R.layout.recycler_item_user, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(v: ViewHolderUsers, position: Int) {
            val collector = collectors[position]

            v.user_name.text = collector.name
            v.pick_user_exp.text = "Set ${collector.name} to do the pickup."

            v.pick_user.setOnClickListener {
                whenUserPicked(collector)
            }
        }

        override fun getItemCount() = collectors.size


    }

    internal inner class ViewHolderUsers (view: View) : RecyclerView.ViewHolder(view) {
        var user_name: TextView = view.findViewById(R.id.user_name)
        val pick_user: RelativeLayout = view.findViewById(R.id.pick_user)
        val pick_user_exp: TextView = view.findViewById(R.id.pick_user_exp)
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

        }

        override fun getItemCount() = donation.images.size


    }

    internal inner class ViewHolderImages (view: View) : RecyclerView.ViewHolder(view) {
        val image_view: ImageView = view.findViewById(R.id.image)
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, collectors: String, organisation: String, donation: String) =
            SetCollectionDate().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_COLLECTORS, collectors)
                    putString(ARG_ORGANISATION,organisation)
                    putString(ARG_DONATION,donation)
                }
            }
    }

    interface SetCollectionDateInterface{
        fun whenSetCollectionDateCollectorPicked(collector: Collectors, donation: Donation, organisation: Organisation, coll_time: Long)
    }
}