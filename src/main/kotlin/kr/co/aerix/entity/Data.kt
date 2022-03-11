package kr.co.aerix.entity

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

const val ISO_8601: String = "yyyy-MM-dd'T'HH:mm:ss"

object Data : Table("data") {
    val id: Column<Long> = long("mbr_no").autoIncrement()
    val value = text("value")
    val receivedtime = text("receivedtime")
    override val primaryKey = PrimaryKey(id);
}

//data class Data_domain(val mbr_no: Long?, val value: String?, val receivedtime: String?, val test: Char?) {}
data class ResultSetToData_domain(var id: Long, var value: String?, var date: Date?);
data class Data_domain(var id: Long, var value: String?, var receivedtime: String?);

data class ParsingData_domain(var x: String?, var y: String?, var z: String?, var receivedtime: String?);

data class GraphData_domain(
    var x: _Data_domain,
    var y: _Data_domain,
    var z: _Data_domain,
    var fft: FFTDatas_domains?
);
data class GraphData_domains(
    var x: List<_Data_domain>,
    var y: List<_Data_domain>,
    var z: List<_Data_domain>,
    var fft: FFTGraphData_domains?
);
data class FFTGraphData_domains(
    var x: List<String>,
    var y: List<String>,
    var z: List<String>,
);

data class FFTDatas_domain(val x: _Data_domain, val y: _Data_domain, val z: _Data_domain);
data class FFTDatas_domains(val x: List<_Data_domain>, val y: List<_Data_domain>, val z: List<_Data_domain>);
/*
val x: String = 시간
val y: String = 값
*/
data class _Data_domain(val x: String, val y: String);

