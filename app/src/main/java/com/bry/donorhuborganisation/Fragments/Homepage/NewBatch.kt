package com.bry.donorhuborganisation.Fragments.Homepage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.bry.donorhuborganisation.Model.Donation
import com.bry.donorhuborganisation.Model.Organisation
import com.bry.donorhuborganisation.Models.Batch
import com.bry.donorhuborganisation.R
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


class NewBatch : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_ORGANISATION = "ARG_ORGANISATION"
    private val ARG_DONATION = "ARG_DONATION"
    private val ARG_BATCH = "ARG_BATCH"
    private lateinit var organisation: Organisation
    private lateinit var donation: Donation
    private var batch: Batch? = null
    private lateinit var listener: NewBatchInterface
    private var location: LatLng = LatLng(0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            if(!it.getString(ARG_ORGANISATION).equals("")){
                organisation = Gson().fromJson(it.getString(ARG_ORGANISATION), Organisation::class.java)
            }
            if(!it.getString(ARG_DONATION).equals("")){
                donation = Gson().fromJson(it.getString(ARG_DONATION) as String, Donation::class.java)
            }
            if(!it.getString(ARG_BATCH).equals("")){
                batch = Gson().fromJson(it.getString(ARG_BATCH) as String, Batch::class.java)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is NewBatchInterface){
            listener = context
        }
    }

    var onLocationPicked: (picked_loc: LatLng) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_new_batch, container, false)
        val set_location_layout: RelativeLayout = va.findViewById(R.id.set_location_layout)
        val finish_layout: RelativeLayout = va.findViewById(R.id.finish_layout)
        val descriptionEditText: EditText = va.findViewById(R.id.descriptionEditText)

        val title: TextView = va.findViewById(R.id.title)
        val create_button_text: TextView = va.findViewById(R.id.create_button_text)

        onLocationPicked = {
            location = it
        }

        set_location_layout.setOnClickListener {
            listener.setBatchLocation()
        }

        finish_layout.setOnClickListener {
            val desc = descriptionEditText.text.toString().trim()
            if(desc.equals("")){
                descriptionEditText.setError("type something!")
            }else{
                if(batch!=null){
                    batch!!.name = desc
                    batch!!.location = location
                    listener.updateBatch(batch!!)
                }else{
                    listener.newBatch(desc, location, donation, organisation)
                }
            }
        }

        if(batch!=null){
            title.text = "Edit Batch"
            create_button_text.text = "Finish"

            descriptionEditText.setText(batch!!.name)
            location = batch!!.location
        }

        return va
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, organisation: String, donation: String, batch: String) =
            NewBatch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_ORGANISATION,organisation)
                    putString(ARG_DONATION,donation)
                    putString(ARG_BATCH, batch)
                }
            }
    }

    interface NewBatchInterface{
        fun setBatchLocation()
        fun newBatch(title: String, location: LatLng, donation: Donation, organisation: Organisation)
        fun updateBatch(batch: Batch)
    }
}