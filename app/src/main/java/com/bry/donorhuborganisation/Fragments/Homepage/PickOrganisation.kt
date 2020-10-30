package com.bry.donorhuborganisation.Fragments.Homepage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bry.donorhuborganisation.Model.Organisation
import com.bry.donorhuborganisation.R
import com.google.gson.Gson
import java.util.ArrayList


class PickOrganisation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_ORGANSIATIONS = "ARG_ORGANSIATIONS"
    private lateinit var organisations: ArrayList<Organisation>
    private lateinit var listener: PickOrganisationInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            organisations = Gson().fromJson(it.getString(ARG_ORGANSIATIONS),
                Organisation.organisation_list::class.java).organisation_list
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is PickOrganisationInterface){
            listener = context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_pick_organisation, container, false)
        val organisations_recyclerview: RecyclerView = va.findViewById(R.id.organisations_recyclerview)

        organisations_recyclerview.adapter = myOrganisationsListAdapter()
        organisations_recyclerview.layoutManager = LinearLayoutManager(context)

        return va
    }

    internal inner class myOrganisationsListAdapter : RecyclerView.Adapter<ViewHolderOrganisations>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderOrganisations {
            val vh = ViewHolderOrganisations(LayoutInflater.from(context)
                .inflate(R.layout.recycler_item_organisation, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(viewHolder: ViewHolderOrganisations, position: Int) {
            var organisation = organisations[position]

            viewHolder.org_name.text = organisation.name
            viewHolder.location_name.text = organisation.location_name

            viewHolder.pick_org.setOnClickListener {
                listener.whenPickOrganisationOrgPicked(organisation)
            }
        }

        override fun getItemCount() = organisations.size

    }

    internal inner class ViewHolderOrganisations (view: View) : RecyclerView.ViewHolder(view) {
        val org_name: TextView = view.findViewById(R.id.org_name)
        val pick_org: RelativeLayout = view.findViewById(R.id.pick_org)
        val location_name: TextView = view.findViewById(R.id.location_name)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, organisations: String) =
            PickOrganisation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_ORGANSIATIONS, organisations)
                }
            }
    }

    interface PickOrganisationInterface{
        fun whenPickOrganisationOrgPicked(organisation: Organisation)
    }
}