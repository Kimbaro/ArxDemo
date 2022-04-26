package kr.co.aerix.model

import kr.co.aerix.entity.Facility

data class FacilityResponse(
    val name: String,
    val workplace_id: Int
) {
    companion object {
        fun of(facility: Facility) = FacilityResponse(name = facility.name, workplace_id = facility.workplace_id)
    }
}
