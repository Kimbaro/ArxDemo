package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.co.aerix.entity.*
import kr.co.aerix.model.SensorResponse
import kr.co.aerix.service.DataService
import kr.co.aerix.service.SensorService
import java.util.logging.Logger


fun Routing.data(service: DataService, findService: SensorService) {

    route("/demo/renderdatas") {
        get {
            println(call.request);
            println(call.request.uri.toString());
            var sensorId = call.request.queryParameters["id"].toString();
            var sensor: SensorResponse = findService.getById(sensorId.toInt())
            if (sensorId.isNullOrEmpty()) {
                throw BadRequestException("아이디가 존재하지 않음");
            }

            var datas: List<GraphData_domain> = service.getRenderData(10, sensor) as List<GraphData_domain>
            var x_data_list = ArrayList<_Data_domain>();
            var y_data_list = ArrayList<_Data_domain>();
            var z_data_list = ArrayList<_Data_domain>();

            for (i in 0 until datas.size) {
                if (datas.get(i) is GraphData_domain) {
                    try {
                        var it: GraphData_domain = datas.get(i)
                        println("${sensor.max} ${sensor.min} ${it.x.y.toDouble()} ${it.x.y.toDouble() < sensor.max!!}    &&    ${it.x.y.toDouble() >= sensor.min!!}");
                        if (sensor.max != null && sensor.min != null) {
                            if (it.x.y.toDouble() < sensor.max!! && it.x.y.toDouble() >= sensor.min!!) {
                                x_data_list.add(it.x)
                            }
                            if (it.y.y.toDouble() < sensor.max!! && it.y.y.toDouble() >= sensor.min!!) {
                                y_data_list.add(it.y)
                            }
                            if (it.z.y.toDouble() < sensor.max!! && it.z.y.toDouble() >= sensor.min!!) {
                                z_data_list.add(it.z)
                            }
                        }
//                        x_data_list.add(it.x)
//                        y_data_list.add(it.y)
//                        z_data_list.add(it.z)
                    } catch (e: NullPointerException) {
                        continue
                    }
                }
            }

            //TODO 기간별조회 쿼리 적용할것 데이터서비스 모듈 부 참조
            val size: Int = x_data_list.size;

            Logger.getLogger("${call.request.uri}").info("${call.request.uri} FFT SIZE ###################### ${size}")
            var fftGraphDatas: FFTGraphData_domains
            /*----------*/
            runBlocking {
                var x_fft: List<String> = ArrayList<String>()
                var y_fft: List<String> = ArrayList<String>()
                var z_fft: List<String> = ArrayList<String>()

                val x = launch {
                    x_fft = service.getFFT(x_data_list.size, x_data_list).toList() as ArrayList<String>
                }
                val y = launch {
                    y_fft = service.getFFT(y_data_list.size, y_data_list).toList() as ArrayList<String>
                }
                val z = launch {
                    z_fft = service.getFFT(z_data_list.size, z_data_list).toList() as ArrayList<String>
                }
                x.join()
                y.join()
                z.join()
                fftGraphDatas = FFTGraphData_domains(x_fft, y_fft, z_fft)
            }
            //println("result === ${fftGraphDatas}");
            /*            */
            call.respond(GraphData_domains(x_data_list, y_data_list, z_data_list, fftGraphDatas))
        }
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
            //startDate != null 기간별조회
            //startDate == null 실시간조회
            if (startDate != null) {
                //TODO 기간별조회 쿼리 적용할것 데이터서비스 모듈 부 참조
                var datas: List<GraphData_domain> = service.getDateRangeData(sensor, startDate, endDate)
                //=============================================
                var x_data_list = ArrayList<_Data_domain>();
                var y_data_list = ArrayList<_Data_domain>();
                var z_data_list = ArrayList<_Data_domain>();
                for (i in 0 until datas.size) {
                    if (datas.get(i) is GraphData_domain) {
                        var it: GraphData_domain = datas.get(i)
                        println("${sensor.max} ${sensor.min} ${it.x.y.toDouble()} ${it.x.y.toDouble() < sensor.max!!}    &&    ${it.x.y.toDouble() >= sensor.min!!}");
                        if (sensor.max != null && sensor.min != null) {
                            if (it.x.y.toDouble() < sensor.max!! && it.x.y.toDouble() >= sensor.min!!) {
                                x_data_list.add(it.x)
                            }
                            if (it.y.y.toDouble() < sensor.max!! && it.y.y.toDouble() >= sensor.min!!) {
                                y_data_list.add(it.y)
                            }
                            if (it.z.y.toDouble() < sensor.max!! && it.z.y.toDouble() >= sensor.min!!) {
                                z_data_list.add(it.z)
                            }
                        }
                    }
                }
                Todo.old_date = null;

                var fftGraphDatas: FFTGraphData_domains
                /*----------*/
                runBlocking {
                    var x_fft: List<String> = ArrayList<String>()
                    var y_fft: List<String> = ArrayList<String>()
                    var z_fft: List<String> = ArrayList<String>()

                    val x = launch {
                        x_fft = service.getFFT(x_data_list.size, x_data_list).toList() as ArrayList<String>
                    }
                    val y = launch {
                        y_fft = service.getFFT(y_data_list.size, y_data_list).toList() as ArrayList<String>
                    }
                    val z = launch {
                        z_fft = service.getFFT(z_data_list.size, z_data_list).toList() as ArrayList<String>
                    }
                    x.join()
                    y.join()
                    z.join()
                    fftGraphDatas = FFTGraphData_domains(x_fft, y_fft, z_fft)
                }

                call.respond(GraphData_domains(x_data_list, y_data_list, z_data_list, fftGraphDatas))
            } else {
                //println("sensorId = ${sensorId}");
                if (isTrend) {
                    var datas: List<GraphData_domain> = service.getRealTimeData(10, sensor);
                    //여기서부터 JTrasform 사용하여 FFT 적용해보자

                    var x_data_list = ArrayList<_Data_domain>();
                    var y_data_list = ArrayList<_Data_domain>();
                    var z_data_list = ArrayList<_Data_domain>();

                    for (i in 0 until datas.size) {
                        if (datas.get(i) is GraphData_domain) {
                            try {
                                var it: GraphData_domain = datas.get(i)
                                println("${sensor.max} ${sensor.min} ${it.x.y.toDouble()} ${it.x.y.toDouble() < sensor.max!!}    &&    ${it.x.y.toDouble() >= sensor.min!!}");
                                if (sensor.max != null && sensor.min != null) {
                                    if (it.x.y.toDouble() < sensor.max!! && it.x.y.toDouble() >= sensor.min!!) {
                                        x_data_list.add(it.x)
                                    }
                                    if (it.y.y.toDouble() < sensor.max!! && it.y.y.toDouble() >= sensor.min!!) {
                                        y_data_list.add(it.y)
                                    }
                                    if (it.z.y.toDouble() < sensor.max!! && it.z.y.toDouble() >= sensor.min!!) {
                                        z_data_list.add(it.z)
                                    }
                                }
                            } catch (e: NullPointerException) {
                                continue
                            }
                        }
                    }

                    Logger.getLogger("${call.request.uri}")
                        .info("${call.request.uri} FFT 테스트 ###################### ${datas.size}")
                    var fftGraphDatas: FFTGraphData_domains
                    /*----------*/
                    runBlocking {
                        var x_fft: List<String> = ArrayList<String>()
                        var y_fft: List<String> = ArrayList<String>()
                        var z_fft: List<String> = ArrayList<String>()

                        val x = launch {
                            x_fft = service.getFFT(x_data_list.size, x_data_list).toList() as ArrayList<String>
                        }
                        val y = launch {
                            y_fft = service.getFFT(y_data_list.size, y_data_list).toList() as ArrayList<String>
                        }
                        val z = launch {
                            z_fft = service.getFFT(z_data_list.size, z_data_list).toList() as ArrayList<String>
                        }
                        x.join()
                        y.join()
                        z.join()
                        fftGraphDatas = FFTGraphData_domains(x_fft, y_fft, z_fft)
                    }
                    //println("result === ${fftGraphDatas}");
                    Logger.getLogger("").info("${call.request.uri} FFT 테스트 EXIT###################")

                    call.respond(
                        GraphData_domains(
                            listOf(x_data_list.get(0)),
                            listOf(y_data_list.get(0)),
                            listOf(z_data_list.get(0)),
                            fftGraphDatas
                        )
                    )
                }
            }
        }
    }
}