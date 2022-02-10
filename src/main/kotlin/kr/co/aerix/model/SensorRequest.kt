package kr.co.aerix.model

import kr.co.aerix.entity.FacilityScheme
import kr.co.aerix.entity.SensorScheme
import kr.co.aerix.entity.SensorScheme.references
import kr.co.aerix.entity.SensorScheme.uniqueIndex

data class SensorRequest(
    val mac: String,
    val model: String,
    val status: String?,
    val name: String,
    val provider:String,
    val workplace_id: Int
);