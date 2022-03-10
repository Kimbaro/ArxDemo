package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.co.aerix.entity.*
import kr.co.aerix.model.SensorResponse
import kr.co.aerix.service.DataService
import kr.co.aerix.service.FFTservice
import kr.co.aerix.service.SensorService
import org.jtransforms.fft.DoubleFFT_1D
import java.util.logging.Logger
import javax.swing.text.html.HTML.Attribute.N


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
                        x_data_list.add(it.x)
                        y_data_list.add(it.y)
                        z_data_list.add(it.z)
                    } catch (e: NullPointerException) {
                        continue
                    }
                }
            }

            /*TODO FFT Calcul X를 예로 테스트 시작함 JTransform 알고리즘*/
            val size: Int = x_data_list.size;

            Logger.getLogger("${call.request.uri}").info("${call.request.uri} FFT 테스트 ###################### ${size}")
            var fftGraphDatas: FFTGraphData_domains
            /*----------*/
            runBlocking {
                var x_fft: List<_Data_domain> = ArrayList<_Data_domain>()
                var y_fft: List<_Data_domain> = ArrayList<_Data_domain>()
                var z_fft: List<_Data_domain> = ArrayList<_Data_domain>()

                val x = launch {
                    x_fft = service.getFFT(x_data_list.size, x_data_list).toList() as ArrayList<_Data_domain>
                }
                val y = launch {
                    y_fft = service.getFFT(y_data_list.size, y_data_list).toList() as ArrayList<_Data_domain>
                }
                val z = launch {
                    z_fft = service.getFFT(z_data_list.size, z_data_list).toList() as ArrayList<_Data_domain>
                }
                x.join()
                y.join()
                z.join()
                fftGraphDatas = FFTGraphData_domains(x_fft, y_fft, z_fft)
            }
            //println("result === ${fftGraphDatas}");
            Logger.getLogger("${call.request.uri}").info("${call.request.uri} FFT 테스트 EXIT################### ${size}")
            /*            */
            call.respond(GraphData_domains(x_data_list, y_data_list, z_data_list, fftGraphDatas))

/*TODO FFT Calcul X를 예로 테스트 시작함  콜럼비아 대학 알고리즘*//*
            //https://ddangeun.tistory.com/48
            val size = x_data_list.size;
            val y = DoubleArray(size) //Imaginary Part
            for (i in 0 until size) {
                y[i] = 0.0
            } //Imaginary Part는 0으로 채운다.

            val x = DoubleArray(size) //Real Part
            for (i in 0 until size) {
                x[i] = Math.sin(2 * Math.PI * 24 * 0.004 * i) + Math.sin(2 * Math.PI * 97 * 0.004 * i)
            }

            val doFFT = FFTservice(size)
            doFFT.fft(x, y) //FFT
            val mag = DoubleArray(size / 2) //magnitude
            for (k in 0 until size / 2) {
                mag[k] = Math.sqrt(Math.pow(x[k], 2.0) + Math.pow(y[k], 2.0))
            }
            *//*----------*/
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
            //startDate == null 실시간조회
            //startDate != null 기간별조회
            if (startDate != null) {
                //TODO 기간별조회 쿼리 적용할것 데이터서비스 모듈 부 참조
                var datas: List<GraphData_domain> = service.getDateRangeData(sensor, startDate, endDate)
                //=============================================

//                var datas: List<GraphData_domain> =
//                    service.getLatestCostN(
//                        -1,
//                        sensor,
//                        isTrend, startDate, endDate
//                    ) as List<GraphData_domain>; //<- FFT 계산식에서 1024개의 데이터를 요구함.
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
                Todo.old_date = null;

                Logger.getLogger("${call.request.uri}").info("${call.request.uri} FFT 테스트 ###################### ${datas.size}")
                var fftGraphDatas: FFTGraphData_domains
                /*----------*/
                runBlocking {
                    var x_fft: List<_Data_domain> = ArrayList<_Data_domain>()
                    var y_fft: List<_Data_domain> = ArrayList<_Data_domain>()
                    var z_fft: List<_Data_domain> = ArrayList<_Data_domain>()

                    val x = launch {
                        x_fft = service.getFFT(x_data_list.size, x_data_list).toList() as ArrayList<_Data_domain>
                    }
                    val y = launch {
                        y_fft = service.getFFT(y_data_list.size, y_data_list).toList() as ArrayList<_Data_domain>
                    }
                    val z = launch {
                        z_fft = service.getFFT(z_data_list.size, z_data_list).toList() as ArrayList<_Data_domain>
                    }
                    x.join()
                    y.join()
                    z.join()
                    fftGraphDatas = FFTGraphData_domains(x_fft, y_fft, z_fft)
                }
                Logger.getLogger("").info("${call.request.uri} FFT 테스트 EXIT###################")

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
                                x_data_list.add(it.x)
                                y_data_list.add(it.y)
                                z_data_list.add(it.z)
                            } catch (e: NullPointerException) {
                                continue
                            }
                        }
                    }

                    Logger.getLogger("${call.request.uri}").info("${call.request.uri} FFT 테스트 ###################### ${datas.size}")
                    var fftGraphDatas: FFTGraphData_domains
                    /*----------*/
                    runBlocking {
                        var x_fft: List<_Data_domain> = ArrayList<_Data_domain>()
                        var y_fft: List<_Data_domain> = ArrayList<_Data_domain>()
                        var z_fft: List<_Data_domain> = ArrayList<_Data_domain>()

                        val x = launch {
                            x_fft = service.getFFT(x_data_list.size, x_data_list).toList() as ArrayList<_Data_domain>
                        }
                        val y = launch {
                            y_fft = service.getFFT(y_data_list.size, y_data_list).toList() as ArrayList<_Data_domain>
                        }
                        val z = launch {
                            z_fft = service.getFFT(z_data_list.size, z_data_list).toList() as ArrayList<_Data_domain>
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