package kr.co.aerix.service

import io.ktor.features.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.WorkplaceResponse
import kr.co.aerix.plugins.query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class WorkplaceService {
    suspend fun getAll(): List<Workplace_domain> = newSuspendedTransaction {
        WorkplaceScheme.selectAll().map {
            toWorkplace(it)
        }
    }

    private fun toWorkplace(row: ResultRow): Workplace_domain {
        println(row.toString());
        return Workplace_domain(
            id = row[WorkplaceScheme.id].value,
            name = row[WorkplaceScheme.name],
        )
    }

    suspend fun getMainDashboardItems(): List<MainDashboardItem> = newSuspendedTransaction {
        WorkplaceScheme.selectAll().orderBy(WorkplaceScheme.id to SortOrder.ASC).map {
            setMainDashboardItems(it)
        }
    }

    private fun setMainDashboardItems(row: ResultRow): MainDashboardItem {
        println(row.toString());
        var items: ArrayList<MainDashboardSubItem> = ArrayList();
        SensorScheme.selectAll().orderBy(SensorScheme.id to SortOrder.ASC).forEach {
            if (row[WorkplaceScheme.id].value == it[SensorScheme.workplace_id]) {
                println("############ : " + it.get(SensorScheme.name));
                items.add(
                    MainDashboardSubItem(
                        id = it.get(SensorScheme.id).value,
                        name = it.get(SensorScheme.name),
                        mac = it.get(SensorScheme.mac),
                        model = it.get(SensorScheme.model),
                        provider = "DEMO"
                    )
                )
            }
        }
        //TODO 여기서부터 진동센서 명칭 받아서 MainDashboardItem과 종합하여 반환해볼것
        return MainDashboardItem(
            id = row[WorkplaceScheme.id].value,
            name = row[WorkplaceScheme.name],
            items = items
        )
    }


//    suspend fun findByMainDashboardItems(): List<Workplace_domain> = newSuspendedTransaction {
//    }


    suspend fun getById(id: Int) = query {
        Workplace.findById(id)?.run(WorkplaceResponse.Companion::of) ?: throw NotFoundException()
    }

    suspend fun new(name: String) = query {
        Workplace.new { this.name = name }
    }
}