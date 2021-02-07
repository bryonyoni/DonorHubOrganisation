package com.bry.donorhuborganisation.Models

import com.google.android.gms.maps.model.LatLng

class Batch(var name: String, var location: LatLng, var batch_id: String, var organisation_id: String) {

    class BatchList(var batches: ArrayList<Batch> = ArrayList())
}