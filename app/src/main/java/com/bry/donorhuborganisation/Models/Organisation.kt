package com.bry.donorhuborganisation.Model

class Organisation(var name: String, var creation_date: Long, var location_name: String) {

    class organisation_list(var organisation_list: ArrayList<Organisation>)
}