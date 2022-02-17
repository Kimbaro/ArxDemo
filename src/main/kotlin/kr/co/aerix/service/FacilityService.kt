package kr.co.aerix.service

import io.ktor.features.*
import kr.co.aerix.entity.Facility
import kr.co.aerix.entity.FacilityScheme
import kr.co.aerix.model.FacilityResponse
import kr.co.aerix.plugins.query
import org.jetbrains.exposed.sql.SortOrder

class FacilityService {
    suspend fun getAll() = query {
        Facility.all().orderBy(FacilityScheme.id to SortOrder.DESC)
            .map(FacilityResponse.Companion::of)
            .toList()
    }

    suspend fun getById(id: Int) = query {
        Facility.findById(id)?.run(FacilityResponse.Companion::of) ?: throw NotFoundException()
    }

    suspend fun new(name: String, workplace_id: Int) = query {
        Facility.new {
            this.name = name
            this.workplace_id = workplace_id
        }
    }
}