package kr.co.aerix.entity

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

const val ISO_8601: String = "yyyy-MM-dd'T'HH:mm:ss"

object Data : Table("data") {
    val id: Column<Int> = integer("mbr_no").autoIncrement()
    val value = text("value")
    val receivedtime = text("receivedtime")
    override val primaryKey = PrimaryKey(id);
}

//data class Data_domain(val mbr_no: Long?, val value: String?, val receivedtime: String?, val test: Char?) {}

data class Data_domain(var id: Int, var value: String?, var receivedtime: String?);

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
    var fft: List<_Data_domain>?
);

data class FFTDatas_domain(val x: _Data_domain, val y: _Data_domain, val z: _Data_domain);
data class FFTDatas_domains(val x: List<_Data_domain>, val y: List<_Data_domain>, val z: List<_Data_domain>);
data class _Data_domain(val x: String, val y: String);

