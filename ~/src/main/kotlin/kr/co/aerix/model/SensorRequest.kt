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
    val provider: String,
    val placeId: Int,
    val min: Double,
    val max: Double,
);

data class SensorPatch(
    val id:Int,
    val mac: String,
    val model: String,
    val status: String?,
    val name: String,
    val provider: String,
    val placeId: Int,
    val min: Double,
    val max: Double,
);


