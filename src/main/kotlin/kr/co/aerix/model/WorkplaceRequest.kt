package kr.co.aerix.model

import kr.co.aerix.entity.Workplace

data class WorkplaceRequest(val name: String)

data class WorkplacePatch(val id: Int, val name: String)
