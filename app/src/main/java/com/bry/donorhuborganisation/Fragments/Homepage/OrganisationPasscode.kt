package com.bry.donorhuborganisation.Fragments.Homepage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.bry.donorhuborganisation.Model.Organisation
import com.bry.donorhuborganisation.R
import com.google.gson.Gson


class OrganisationPasscode : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var listener: OrganisationPasscodeInterface
    private val ARG_ORGANISATION = "ARG_ORGANISATION"
    private lateinit var the_organisation: Organisation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            the_organisation = Gson().fromJson(it.getString(ARG_ORGANISATION), Organisation::class.java)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OrganisationPasscodeInterface){
            listener = context
        }
    }

    var whenPasscodeFailed: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_organisation_passcode, container, false)
        val PasswordEditText: EditText = va.findViewById(R.id.PasswordEditText)
        val continue_layout: RelativeLayout = va.findViewById(R.id.continue_layout)
        val keyPasswordEditText: EditText = va.findViewById(R.id.keyPasswordEditText)


        continue_layout.setOnClickListener {
            val code = PasswordEditText.text.toString().trim()
            val key_code = keyPasswordEditText.text.toString().trim()
            if(code.equals("")){
                PasswordEditText.error = "Type something"
            }else if(key_code.equals("")){
                keyPasswordEditText.setError("Type something")
            }
            else{
                listener.onOrganisationPasscodeSubmitPasscode(code,the_organisation, key_code)
//                listener.onOrganisationKeyPasscodeSubmitKey(key_code, the_organisation)
            }
        }

        whenPasscodeFailed = {
            PasswordEditText.setError("That didn't work")
            keyPasswordEditText.setError("Recheck this code and retry")
        }

        return va
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, organisation: String) =
            OrganisationPasscode().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_ORGANISATION,organisation)
                }
            }
    }

    interface OrganisationPasscodeInterface{
        fun onOrganisationPasscodeSubmitPasscode(code: String, organisation: Organisation, key_code: String)

        fun onOrganisationKeyPasscodeSubmitKey(key_code: String, organisation: Organisation)
    }

}