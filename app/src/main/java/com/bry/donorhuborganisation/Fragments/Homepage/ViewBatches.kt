package com.bry.donorhuborganisation.Fragments.Homepage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bry.donorhuborganisation.Models.Batch
import com.bry.donorhuborganisation.R
import com.google.gson.Gson


class ViewBatches : Fragment() {
    // TODO: Rename and change types of parameters
    private  val ARG_PARAM1 = "param1"
    private  val ARG_PARAM2 = "param2"
    private val ARG_BATCHES = "ARG_BATCHES"
    private var param1: String? = null
    private var param2: String? = null
    private var batches: ArrayList<Batch> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            batches = Gson().fromJson(it.getString(ARG_BATCHES) as String, Batch.BatchList::class.java).batches
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_view_batches, container, false)
        val new_batch: ImageView = va.findViewById(R.id.new_batch)
        val batches_recyclerview: RecyclerView = va.findViewById(R.id.batches_recyclerview)


        batches_recyclerview.adapter = myOrganisationsListAdapter()
        batches_recyclerview.layoutManager = LinearLayoutManager(context)

        return va
    }


    internal inner class myOrganisationsListAdapter : RecyclerView.Adapter<ViewHolderBatches>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderBatches {
            val vh = ViewHolderBatches(LayoutInflater.from(context)
                .inflate(R.layout.recycler_item_batches, viewGroup, false))
            return vh
        }

        override fun onBindViewHolder(viewHolder: ViewHolderBatches, position: Int) {
            var batch = batches[position]

            viewHolder.batch_name.text = batch.name
            viewHolder.pick_batch.visibility = View.GONE
        }

        override fun getItemCount() = batches.size

    }

    internal inner class ViewHolderBatches (view: View) : RecyclerView.ViewHolder(view) {
        var batch_name: TextView = view.findViewById(R.id.batch_name)
        var pick_batch: RelativeLayout = view.findViewById(R.id.pick_batch)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, batches: String) =
            ViewBatches().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_BATCHES, batches)
                }
            }
    }
}