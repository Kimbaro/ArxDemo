package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.SensorResponse
import kr.co.aerix.service.DataService
import kr.co.aerix.service.SensorService


fun Routing.data(service: DataService, findService: SensorService) {

//    fun <T : Any> String.execAndMap(transform: (ResultSet) -> T): List<T> {
//        val result = arrayListOf<T>()
//        TransactionManager.current().exec("") { rs ->
//            while (rs.next()) {
//                result += transform(rs)
//            }
//        }
//        return result
//    }


    route("/demo/renderdatas") {
        /*get {
            println(call.request);
            println(call.request.uri.toString());
            var isTrend = call.request.queryParameters["isTrend"].toBoolean()
            var sensorId = call.request.queryParameters["id"].toString();
            call.respond(service.getAll())

            var sensor: SensorResponse = findService.getById(sensorId.toInt())

            var datas: List<GraphData_domain> =
                service.getLatestCostN(
                    1024,
                    sensor,
                    isTrend
                ) as List<GraphData_domain>; //<- FFT 계산식에서 1024개의 데이터를 요구함.
            datas.forEach {

            }
            var result: GraphData_domain? = datas.get(0)
            call.respond(
                GraphData_domains(
                    listOf(result.x),
                    listOf(result.y),
                    listOf(result.z),
                    service.addMAXFFT(datas)
                )
            )
        }*/
    }

    route("/demo/datas") {
        /*get {
            call.respond(service.getAll())
        }*/
        get {
            println(call.request);
            println(call.request.uri.toString());
            var isTrend = call.request.queryParameters["isTrend"].toBoolean()
            var sensorId = call.request.queryParameters["id"].toString();
            var startDate = call.request.queryParameters["start"]?.toLong();
            var endDate = call.request.queryParameters["end"]?.toLong();
            var result: GraphData_domain? = null
            var sensor: SensorResponse = findService.getById(sensorId.toInt())
            if (sensorId.isNullOrEmpty()) {
                throw BadRequestException("아이디가 존재하지 않음");
            }
            if (startDate != null) {
                var datas: List<GraphData_domain> =
                    service.getLatestCostN(
                        -1,
                        sensor,
                        isTrend, startDate, endDate
                    ) as List<GraphData_domain>; //<- FFT 계산식에서 1024개의 데이터를 요구함.
                //전체인경우
                // val fftX2: MutableMap<String, Double>? = service.getMaxValue(fft_datas.get("X")!!)
//            val fftY2: MutableMap<String, Double>? = service.getMaxValue(fft_datas.get("Y")!!)
//            val fftZ2: MutableMap<String, Double>? = service.getMaxValue(fft_datas.get("Z")!!)
//            println(fftX2!!.get("max").toString())
//            println(fftY2!!.get("max").toString())
//            println(fftZ2!!.get("max").toString())
                var x_data_list = ArrayList<_Data_domain>();
                var y_data_list = ArrayList<_Data_domain>();
                var z_data_list = ArrayList<_Data_domain>();
                for (i in 0 until datas.size) {
                    if (datas.get(i) is GraphData_domain) {
                        var it: GraphData_domain = datas.get(i)
                        x_data_list.add(it.x)
                        y_data_list.add(it.y)
                        z_data_list.add(it.z)
                    }
                }
//                datas.forEach {
//                    //blur blur
//                    x_data_list.add(it.x)
//                    y_data_list.add(it.y)
//                    z_data_list.add(it.z)
//                }
                Todo.old_date = null;
                //println("FFT ================ ${x_data_list}");
                call.respond(GraphData_domains(x_data_list, y_data_list, z_data_list, null))
                //call.respond(datas)
            } else {
                //println("sensorId = ${sensorId}");
                if (isTrend) {
                    var datas: List<GraphData_domain> =
                        service.getLatestCostN(
                            1024,
                            sensor,
                            isTrend
                        ) as List<GraphData_domain>; //<- FFT 계산식에서 1024개의 데이터를 요구함.
                    //result

                    result = datas.get(0)
//                result.fft = fftdatasDomain;
                    call.respond(
                        GraphData_domains(
                            listOf(result.x!!),
                            listOf(result.y!!),
                            listOf(result.z!!),
                            service.addMAXFFT(datas)
                        )
                    )
                }
            }
        }
    }
}