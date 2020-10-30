package com.bry.donorhuborganisation.Fragments.Homepage

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.bry.donorhuborganisation.Constants
import com.bry.donorhuborganisation.Model.Organisation
import com.bry.donorhuborganisation.R
import com.google.gson.Gson
import java.util.*


class AddOrganisationMember : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_ORGANISATION = "ARG_ORGANISATION"
    private lateinit var the_organisation: Organisation
    private lateinit var listener: AddOrganisationMember

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            the_organisation = Gson().fromJson(it.getString(ARG_ORGANISATION), Organisation::class.java)
        }
    }

    var isPasscodeSet: () -> Unit = {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is AddOrganisationMember){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_add_organisation_member, container, false)
        val new_code: TextView = va.findViewById(R.id.new_code)
        val reload_layout: RelativeLayout = va.findViewById(R.id.reload_layout)


        reload_layout.setOnClickListener {
            val i = (Random().nextInt(900000) + 100000).toLong()
            new_code.visibility = View.VISIBLE
            new_code.text = "Code : ${i}"

            listener.generatePasscodeClicked(the_organisation, i)
        }

        isPasscodeSet = {
            Handler().postDelayed({
                new_code.visibility = View.GONE
                new_code.text = ""
                reload_layout.visibility = View.VISIBLE

            }, Constants().otp_expiration_time)

            reload_layout.visibility = View.GONE
        }

        return va
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, organisation: String) =
            AddOrganisationMember().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_ORGANISATION,organisation)
                }
            }
    }

    interface AddOrganisationMember{
        fun generatePasscodeClicked(organisation: Organisation, code: Long)
    }
}