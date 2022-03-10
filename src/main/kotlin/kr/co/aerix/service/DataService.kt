package kr.co.aerix.service

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.util.*
import kr.co.aerix.entity.*
import kr.co.aerix.model.SensorResponse
import kr.co.aerix.plugins.DatabaseInitializer
import kr.co.aerix.plugins.query
import kr.co.aerix.plugins.query_psql
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.jtransforms.fft.DoubleFFT_1D
import java.sql.ResultSet
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DataService {
    /*기간별조회*/
    @OptIn(InternalAPI::class)
    suspend fun getDateRangeData(
        sensor: SensorResponse,
        startDate: Long? = null,
        endDate: Long? = null
    ): List<GraphData_domain> = query_psql {
        ("SELECT data.mbr_no,\n" +
                "                EXTRACT(EPOCH FROM (TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\\\"T\\\"HH24:MI:SS'))) * 1000 as receivedtime,\n" +
                "                value\n" +
                "                FROM data\n" +
                "                where\n" +
                "                (EXTRACT(EPOCH FROM (TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\\\"T\\\"HH24:MI:SS'))) * 1000) >= ${startDate}\n" +
                "                AND (EXTRACT(EPOCH FROM (TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\\\"T\\\"HH24:MI:SS'))) * 1000) < ${endDate} AND\n" +
                "                data.mac ='${sensor.mac}'\n" +
                "                order by(data.mbr_no) desc")
            .getDefaultExecAndMap {

                var jsonData2: JsonObject = JsonParser().parse(it.getString("value")).asJsonObject;
                var graphdataDomain: GraphData_domain? = null
                println(jsonData2);
                jsonData2.keySet().toList().forEach { s ->
                    var parsing_datas = jsonData2.get("parsing").toString();
                    var parsing_list: List<String> =
                        parsing_datas.substring(1, parsing_datas.length - 1).split(",").toList()
                    var currentDate =
                        Timestamp.valueOf(Date(it.getLong("receivedtime")).toLocalDateTime().plusHours(9L));
                    var x: _Data_domain =
                        _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(0))
                    var y: _Data_domain =
                        _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(1))
                    var z: _Data_domain =
                        _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(2))
                    var result: GraphData_domain = GraphData_domain(x, y, z, null)
                    graphdataDomain = result
                }
                graphdataDomain!!
            }
    }

    /*실시간조회*/
    @OptIn(InternalAPI::class)
    suspend fun getRealTimeData(
        minutes: Int,
        sensor: SensorResponse
    ): List<GraphData_domain> = query_psql {
        ("SELECT data.mbr_no,\n" +
                "EXTRACT(EPOCH FROM (TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"'))) * 1000 as receivedtime,\n" +
                "data.value\n" +
                "FROM data \n" +
                "where data.mac ='${sensor.mac}' \n" +
                "and TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') >= now()- interval '${minutes} minutes' \n" +
                "order by(data.mbr_no) desc;")
            .getDefaultExecAndMap {
                var jsonData2: JsonObject = JsonParser().parse(it.getString("value")).asJsonObject;
                var graphdataDomain: GraphData_domain? = null
                //println(jsonData2);
                jsonData2.keySet().toList().forEach { s ->
                    var parsing_datas = jsonData2.get("parsing").toString();
                    var parsing_list: List<String> =
                        parsing_datas.substring(1, parsing_datas.length - 1).split(",").toList()
                    var currentDate =
                        Timestamp.valueOf(Date(it.getLong("receivedtime")).toLocalDateTime().plusHours(9L));
                    var x: _Data_domain =
                        _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(0))
                    var y: _Data_domain =
                        _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(1))
                    var z: _Data_domain =
                        _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(2))
                    var result: GraphData_domain = GraphData_domain(x, y, z, null)
                    graphdataDomain = result
                }
                graphdataDomain!!
            }
    }

    private fun <T : Any> String.getDefaultExecAndMap(transform: (ResultSet) -> T): List<T> {
        val result = arrayListOf<T>()
        TransactionManager.current().exec(this.toString()) { rs ->
            while (rs.next()) {
                result += transform(rs)
            }
        }
        return result
    }

    @OptIn(InternalAPI::class)
    suspend fun getRenderData(
        minutes: Int,
        sensor: SensorResponse
    ): List<GraphData_domain> = query_psql {
        ("SELECT data.mbr_no,\n" +
                "EXTRACT(EPOCH FROM (TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"'))) * 1000 as receivedtime,\n" +
                "data.mac,\n" +
                "data.value\n" +
                "FROM data \n" +
                "where data.mac ='${sensor.mac}' \n" +
                "and TO_TIMESTAMP(replace(data.receivedtime,'\"',''), 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') >= now()- interval '${minutes} minutes' \n" +
                "order by(data.mbr_no) desc limit 1024;")
            .getRenderDataExecAndMap {
                var graphdataDomain: GraphData_domain? = null
                var jsonData: JsonObject = JsonParser().parse(it.getString("value")).asJsonObject;
//                println("테스트1 +=======${it.getString("mac")}")
//                println("테스트2 +=======${jsonData}")
                var currentDate =
                    Timestamp.valueOf(Date(it.getLong("receivedtime")).toLocalDateTime().plusHours(9L));
                jsonData.keySet().toList().forEach { s ->
                    if (sensor.mac.equals(it.getString("mac"))) {
                        var parsing_datas = jsonData.get("parsing").toString();
                        var parsing_list: List<String> =
                            parsing_datas.substring(1, parsing_datas.length - 1).split(",").toList()
                        var x: _Data_domain =
                            _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(0))
                        var y: _Data_domain =
                            _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(1))
                        var z: _Data_domain =
                            _Data_domain(x = currentDate.toGMTString(), y = parsing_list.get(2))
                        var result: GraphData_domain = GraphData_domain(x, y, z, null)
                        //1024 길이의 데이터가 있어야 FFT 값 계산이 가능하므로 다음 로직에서 만듭니다.
                        //TODO 중요, 아래 주석처리된 로직이 fft값을 계산하는데, 처리 시간이 너무 길어서 다른 방안을 생각해봐야한다.
                        //TODO 1. 예를 들면 DB에 한번만 접근해서 특정기간 데이터를 모두 불러온 다음에 처리.
                        graphdataDomain = result
                    }
                }
                graphdataDomain!!
                //println("${it.getLong("mbr_no")}, ${it.getString("value")}, ${Date(it.getLong("receivedtime"))}");
            }
    }

    private fun <T : Any> String.getRenderDataExecAndMap(transform: (ResultSet) -> T): List<T> {
        var targetTime: Long = Date().time;
        val result = arrayListOf<T>()
        TransactionManager.current().exec(this.toString()) { rs ->
            //시간 60s = 60000
            while (rs.next()) {
                if (targetTime - Date(rs.getLong("receivedtime")).time >= 1000) {
                    targetTime = Date(rs.getLong("receivedtime")).time
                    result += transform(rs)
                }
            }
        }
        return result
    }

    public suspend fun getFFT(size: Int, data_list: ArrayList<_Data_domain>): ArrayList<_Data_domain> {
        val imaginary_part = DoubleArray(size) //Imaginary Part
        val fft_list = ArrayList<_Data_domain>();
        for (i in 0 until size) {
            imaginary_part[i] = 0.0
        }
        val real_part = DoubleArray(size) //Real Part
        for (i in 0 until size) {
            real_part[i] = data_list.get(i).y.toDouble()
        }
        val fft_part = DoubleArray(2 * size) //fft 수행할 배열 사이즈 2N
        for (k in 0 until size) {
            fft_part[2 * k] = real_part.get(k) //real_part
            fft_part[2 * k + 1] = imaginary_part.get(k) //imaginary_part
        }
        val fft = DoubleFFT_1D(size.toLong()) //1차원의 fft 수행
        fft.complexForward(fft_part) //a 배열에 output overwrite
        val mag = DoubleArray(size / 2)
        for (k in 0 until size / 2) {
            mag[k] = Math.sqrt(Math.pow(fft_part[2 * k], 2.0) + Math.pow(fft_part[2 * k + 1], 2.0))
            fft_list.add(_Data_domain(Date().toGMTString(), String.format("%.2f", mag[k])))
        }
        return fft_list
    }


    // old code#########################################
    suspend fun getLatestCostN(
        n: Int,
        sensor: SensorResponse,
        isTrend: Boolean,
        startDate: Long? = null,
        endDate: Long? = null
    ) =
        query_psql {
            when (sensor.model) {
                "T435" -> {
                    if (isTrend && startDate == null) {
                        println("최근데이터 검색");


                        //최근 데이터 검색 selectOne
                        Data.selectAll().orderBy(Data.id to SortOrder.DESC).limit(n)
                            .map {
                                toTrendTrueData(
                                    Data_domain(it[Data.id], it[Data.value], it[Data.receivedtime]),
                                    sensor
                                )
                            }
                    } else {
                        println("날짜 범위내 검색");
                        //날짜범위내 검색 selectAll
                        val dateParser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                        var startDate: Date = Date(startDate!!);
                        var endDate: Date = Date(endDate!!);
                        var targetDate: Date? = null;
                        //println("${startDate}   ${endDate}");
                        Data.selectAll().orderBy(Data.id to SortOrder.DESC)
                            .mapNotNull {
                                //target date convert
                                var receivedtimeToString: String =
                                    it[Data.receivedtime].toString()
                                        .substring(1, it[Data.receivedtime].toString().length - 1)
                                receivedtimeToString = "${receivedtimeToString.split("T").get(0)} ${
                                    receivedtimeToString.split("T").get(1)
                                }"
                                targetDate = dateParser.parse(receivedtimeToString);
                                //println("${targetDate}");
                                //start <= target <= end
                                if (targetDate!! >= startDate && targetDate!! <= endDate) {
                                    toTrendFalseData(
                                        it,
                                        sensor
                                    )
                                } else {
                                }
                            }
                    }
                }
                else -> {
                    Data.selectAll().orderBy(Data.id to SortOrder.DESC).limit(n).mapNotNull { toDataDefault(it) }
                }
            }
        }

    //suspend fun getFFT scada의 getFftXYZData를 참고한다. 1024
    private fun toTrendFalseData(
        row: ResultRow,
        sensor: SensorResponse
    ): GraphData_domain? {
        var graphdataDomain: GraphData_domain? = null
        var jsonData: JsonObject = JsonParser().parse(row[Data.value]).asJsonObject;
        jsonData.keySet().toList().forEach { s ->
            if (sensor.mac.equals(s.toString())) {
                jsonData = JsonParser().parse(jsonData.get(s).toString()).asJsonObject
                //날짜-시간 정보
                var receivedtimeToString: String =
                    row[Data.receivedtime].toString().substring(1, row[Data.receivedtime].toString().length - 1)
                        .split(".").get(0)
                val dateParser = SimpleDateFormat(ISO_8601, Locale.KOREA)
                var date = dateParser.parse(receivedtimeToString)

                //# 임시 , 이후 exec 명령어로 해결해볼것
                val cal = Calendar.getInstance()
                cal.time = date
                cal.add(Calendar.HOUR, 9);
                date = cal.time
                if (Todo.old_date != null) {
                    if ((Todo.old_date!!.time - date.time) >= 120000) {
                        println("${Todo.old_date!!.time}   -   ${date.time} = ${Todo.old_date!!.time - date.time}");
                        var parsing_datas = jsonData.get("parsing").toString();
                        var parsing_list: List<String> =
                            parsing_datas.substring(1, parsing_datas.length - 1).split(",").toList()
                        var x: _Data_domain = _Data_domain(x = date.toGMTString(), y = parsing_list.get(0))
                        var y: _Data_domain = _Data_domain(x = date.toGMTString(), y = parsing_list.get(1))
                        var z: _Data_domain = _Data_domain(x = date.toGMTString(), y = parsing_list.get(2))
                        var result: GraphData_domain = GraphData_domain(x, y, z, null)
                        Todo.old_date = date;
                        //1024 길이의 데이터가 있어야 FFT 값 계산이 가능하므로 다음 로직에서 만듭니다.
                        //TODO 중요, 아래 주석처리된 로직이 fft값을 계산하는데, 처리 시간이 너무 길어서 다른 방안을 생각해봐야한다.
                        //TODO 1. 예를 들면 DB에 한번만 접근해서 특정기간 데이터를 모두 불러온 다음에 처리.
//                var fft_range: List<GraphData_domain> = Data.selectAll().limit(512).mapNotNull {
//                    toGraphData_domain(row = row, it = it, sensor = sensor, s = s)
//                }
//                if (!fft_range.isNotEmpty()) {
//                    result.fft = addMAXFFT(fft_range)
//                }
                        graphdataDomain = result
                    }
                } else {
                    Todo.old_date = date;
                }
            }
        }
        return graphdataDomain
    }

    //    private fun toTrendTrueData(row: ResultRow, sensor: SensorResponse): GraphData_domain? {
    private fun toTrendTrueData(row: Data_domain, sensor: SensorResponse): GraphData_domain? {
        var graphdataDomain: GraphData_domain? = null
        var jsonData: JsonObject = JsonParser().parse(row.value).asJsonObject;
        jsonData.keySet().toList().forEach { s ->
            if (sensor.mac.equals(s.toString())) {
                jsonData = JsonParser().parse(jsonData.get(s).toString()).asJsonObject
                //날짜-시간 정보
                var receivedtimeToString: String =
                    row.receivedtime.toString().substring(1, row.receivedtime.toString().length - 1)
                        .split(".").get(0)
                val dateParser = SimpleDateFormat(ISO_8601, Locale.KOREA)
                var date = dateParser.parse(receivedtimeToString)

                //# 임시 , 이후 exec 명령어로 해결해볼것
                val cal = Calendar.getInstance()
                cal.time = date
                cal.add(Calendar.HOUR, 9);
                cal.time
                date = cal.time

                //println(date.toGMTString()) //12 Feb 2022 07:10:16 GMT
                //println(date.time) //1644649816636
                //데이터 파싱
                var parsing_datas = jsonData.get("parsing").toString();
                var parsing_list: List<String> =
                    parsing_datas.substring(1, parsing_datas.length - 1).split(",").toList()
//                println(
//                    "AccX = ${parsing_list.get(0)}" +
//                            " AccY = ${parsing_list.get(1)}" +
//                            " AccZ = ${parsing_list.get(2)}" +
//                            " VelX = ${parsing_list.get(3)}" +
//                            " VelY = ${parsing_list.get(4)}" +
//                            " VelZ = ${parsing_list.get(5)}" +
//                            " vbat = ${parsing_list.get(6)}"
//                )
                var x: _Data_domain = _Data_domain(x = date.toGMTString(), y = parsing_list.get(0))
                var y: _Data_domain = _Data_domain(x = date.toGMTString(), y = parsing_list.get(1))
                var z: _Data_domain = _Data_domain(x = date.toGMTString(), y = parsing_list.get(2))
                graphdataDomain = GraphData_domain(x, y, z, null)
            }
        }
        return graphdataDomain
    }

    /*
    * ==============   FFT formula used in SCADA   ==============
    * */
    fun isPowerOfTwo(size: Int): Boolean {
        return size != 0 && size and size - 1 == 0
    }

    fun getPowerOfTwo(size: Int): Int {
        for (i in 1024 downTo 1) {
            if (i != 0 && i and i - 1 == 0 && size > i) {
                return i
            }
        }
        return 0
    }

    fun addMAXFFT(datas: List<GraphData_domain>): List<_Data_domain>? {
        try {
            var fft_datas: Map<String, DoubleArray?>? = getFftXYZData(datas)
            //set FFT
            var fft_data_x: MutableMap<String, Double> = getMaxValue(fft_datas?.get("X")!!)!!
            var fft_data_y: MutableMap<String, Double> = getMaxValue(fft_datas?.get("Y")!!)!!
            var fft_data_z: MutableMap<String, Double> = getMaxValue(fft_datas?.get("Z")!!)!!
            var x_fft: _Data_domain =
                _Data_domain(
                    x = "${datas.get(0).x.x}",
                    y = "${fft_data_x.get("max")}"
                )
            var y_fft: _Data_domain =
                _Data_domain(
                    x = "${datas.get(0).y.x}",
                    y = "${fft_data_y.get("max")}"
                )
            var z_fft: _Data_domain =
                _Data_domain(
                    x = "${datas.get(0).z.x}",
                    y = "${fft_data_z.get("max")}"
                )
//            var fftdatasDomains: FFTDatas_domains =
//                FFTDatas_domains(x = listOf(x_fft), y = listOf(y_fft), z = listOf(z_fft))
            return listOf(x_fft, y_fft, z_fft)
        } catch (e: NullPointerException) {
            //e.printStackTrace()
        }
        return null
    }

    //TODO Fft 배열 1024 만큼 데이터를 가져와 그래프를 구성함
    fun getFftXYZData(data: List<GraphData_domain>?): Map<String, DoubleArray?> {
        var dataSize = data!!.size
        val isPowerOfTwo: Boolean = isPowerOfTwo(data.size)
        if (!isPowerOfTwo) {
            dataSize = getPowerOfTwo(data.size)
        }

        val frequencyDomainX: DoubleArray = DoubleArray(dataSize)
        val frequencyDomainY: DoubleArray = DoubleArray(dataSize)
        val frequencyDomainZ: DoubleArray = DoubleArray(dataSize)

        for (i in 0 until dataSize) {
            frequencyDomainX[i] = data[i].x.y.toDouble()
            frequencyDomainY[i] = data[i].y.y.toDouble()
            frequencyDomainZ[i] = data[i].z.y.toDouble()
        }

        //32-bitFFT - 2048~1024, 16-bitFFT - 1024~512, 8-bitFFT - 512~256
        val powerSpectrumX: DoubleArray? = if (frequencyDomainX.size <= 1024 && frequencyDomainX.size >= 32) setFftData(
            frequencyDomainX.size,
            frequencyDomainX
        ) else null
        val powerSpectrumY: DoubleArray? = if (frequencyDomainY.size <= 1024 && frequencyDomainY.size >= 32) setFftData(
            frequencyDomainY.size,
            frequencyDomainY
        ) else null
        val powerSpectrumZ: DoubleArray? = if (frequencyDomainZ.size <= 1024 && frequencyDomainZ.size >= 32) setFftData(
            frequencyDomainZ.size,
            frequencyDomainZ
        ) else null
        val map: HashMap<String, DoubleArray?> = HashMap()
        map["X"] = powerSpectrumX
        map["Y"] = powerSpectrumY
        map["Z"] = powerSpectrumZ
        return map
    }

    //TODO Fft 배열 1024 중에서 가장큰 하나의 값을 가져와 패널에 보여줌
    fun getMaxValue(numbers: DoubleArray): MutableMap<String, Double>? {
        val map: MutableMap<String, Double> = HashMap()
        var maxValue = numbers[0]
        var maxIndex = 0
        var amplitude = 0.0
        for (i in 1 until numbers.size) {
            amplitude += numbers[i] * numbers[i]
            if (numbers[i] > maxValue) {
                maxValue = numbers[i]
                maxIndex = i
            }
        }
        map["max"] = maxValue
        map["maxIndex"] = maxIndex.toDouble()
        // overall
        val numerator = Math.sqrt(amplitude)
        val noiseBandWidth = Math.sqrt(1.5)
        val overall = numerator / noiseBandWidth
        map["overall"] = overall
        return map
    }

    fun setFftData(frequency: Int, frequencyDomain: DoubleArray): DoubleArray {
        var powerSpectrum = DoubleArray(0)
        val fft = FastFourierTransformer(DftNormalization.STANDARD)
        try {
            val spectrum = fft.transform(frequencyDomain, TransformType.FORWARD)

            powerSpectrum = DoubleArray(frequencyDomain.size / 2)
            for (i in powerSpectrum.indices) {
                val abs = spectrum[i].abs() / frequency
                powerSpectrum[i] = 2 * abs
            }

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return powerSpectrum
    }

    //검색에 사용되는 함수
    private fun toGraphData_domain(
        row: ResultRow,
        it: ResultRow,
        sensor: SensorResponse,
        s: String
    ): GraphData_domain? {
        if (row[Data.id] >= it.get(Data.id) && it.get(Data.id) >= row[Data.id] - 512 && it.get(Data.id) >= 1) {
            var jsonData: JsonObject = JsonParser().parse(row[Data.value]).asJsonObject;
            if (sensor.mac.equals(s.toString())) {
                jsonData = JsonParser().parse(jsonData.get(s).toString()).asJsonObject
                //날짜-시간 정보
                var receivedtimeToString: String =
                    row[Data.receivedtime].toString().substring(1, row[Data.receivedtime].toString().length - 1)
                val dateParser = SimpleDateFormat(ISO_8601, Locale.KOREA)
                var date = dateParser.parse(receivedtimeToString + "Z")

                var parsing_datas = jsonData.get("parsing").toString();
                var parsing_list: List<String> =
                    parsing_datas.substring(1, parsing_datas.length - 1).split(",").toList()

                var x: _Data_domain = _Data_domain(x = date.toString(), y = parsing_list.get(0))
                var y: _Data_domain = _Data_domain(x = date.toString(), y = parsing_list.get(1))
                var z: _Data_domain = _Data_domain(x = date.toString(), y = parsing_list.get(2))

                var temp: GraphData_domain = GraphData_domain(x, y, z, null)
                return temp
            }
        }
        return null
    }

    private fun toDataDefault(row: ResultRow): Data_domain {
        var temp = Data_domain(
            id = row[Data.id],
            value = "미구현모델명칭",
            receivedtime = row[Data.receivedtime].toString(),
        );
        return temp
    }
}

