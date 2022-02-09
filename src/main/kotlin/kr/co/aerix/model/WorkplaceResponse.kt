package kr.co.aerix.model

import kr.co.aerix.entity.Workplace

data class WorkplaceResponse(val name: String) {
    companion object {
        fun of(workplace: Workplace) = WorkplaceRequest(name = workplace.name)
    }
}