package com.bry.donorhuborganisation.Model

class Donation(var description: String, var creation_time: Long, var donation_id: String) {
    var is_taken_down: Boolean =  false
    var images: ArrayList<donation_image> = ArrayList()
    var activies: ArrayList<activity> = ArrayList()
    var organisation_id: String = ""

    class donation_list(var donation_list: ArrayList<Donation>)

    class donation_image (var name: String){
    }

    class activity(var explanation: String, var time: Long)
}