package com.bry.donorhuborganisation.Model

class Organisation(var name: String, var creation_date: Long, var location_name: String, var org_id: String) {
    var members: ArrayList<String> = ArrayList()
    var admin_members: ArrayList<String> = ArrayList()

    class organisation_list(var organisation_list: ArrayList<Organisation>)
}