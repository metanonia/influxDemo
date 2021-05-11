package com.metanonia.influx.binance;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ImportMySql {
    //UTC--2021-05-11T11-35-00.64731000Z--cc096f00f623510498f4dfa1f25a4bc3187a2824.json : fx`)y8a*5F
    public static void main(String[] args) {
        if(args.length != 3) {
            System.exit(0);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        SimpleDateFormat mdf = new SimpleDateFormat("mm");
        mdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String token = args[0];
        String bucket = "blockchain_min";
        String org = "metanonia";
        InfluxDBClient client = InfluxDBClientFactory.create("http://dvlp.metanonia.com:8086", token.toCharArray());
        Web3Api web3Api = new Web3Api();

        try {
            MySqlApi mySqlApi = new MySqlApi(args[1], args[2]);

            String query = "from(bucket: \""+bucket+"\") |> range(start: -24h ) ";
            List<FluxTable> tables = client.getQueryApi().query(query, org);

            for (int i = 0; i < tables.size(); i++) {
                FluxTable table = tables.get(i);
                List<FluxRecord> records = table.getRecords();
                for (int j = 0; j < records.size(); j++) {
                    FluxRecord record = records.get(j);
                    Map<String, Object> row = record.getValues();
                    ZonedDateTime time = ZonedDateTime.parse(row.get("_time").toString());
                    Date date = Date.from(time.toInstant());
                    Double value = (Double) row.get("_value");
                    String msg = sdf.format(date) + ":" + String.format("%f", value);

                    String curStr = DigestUtils.sha512Hex(msg);
                    BigInteger curHash = new BigInteger(curStr,16);
                    String hash = mySqlApi.getLastSumHash();
                    BigInteger lastHash = null;
                    BigInteger sumHash = null;
                    if(hash == null) sumHash = curHash;
                    else {
                        lastHash = new BigInteger(hash, 16);
                        sumHash = lastHash.xor(curHash);
                    }

                    int ret = mySqlApi.insertData(sdf.format(date), value, curStr, sumHash.toString(16));
                    if(ret != 0) {
                        // 10분마다 블록체인에 저장
                        int mm = Integer.parseInt(mdf.format(date));
                        if (mm % 10 == 0) {
                            JSONObject jObj = new JSONObject();
                            jObj.put("message", msg);
                            jObj.put("curHash", curStr);
                            jObj.put("sumHash", sumHash.toString(16));

                            String rmsg = web3Api.sendData(jObj.toString());
                            Thread.sleep(60000);    // batch 처리.. 블록생성 시간 대응
                            System.out.println(rmsg);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
