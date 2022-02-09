package kr.co.aerix.model

import kr.co.aerix.entity.Facility

data class FacilityRequest(
    val name: String,
    val workplace_id: Int
);
