package com.metanonia.influx.demo;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.util.List;

public class GetInfluxDb {
    static String token = "wy2DoXjn4G6vdpLZIgNXXXWWF3s72QvshwW9n6MNk1uurazz3fiUx3RFkYBL3pe5bepJx8TOzJl5p9QuOENnvQ==";

    public static void main(String[] args) {
        String bucket = "blockchain";
        String org = "metanonia";
        InfluxDBClient client = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray());

        try {
            String query = "from(bucket: \""+bucket+"\") |> range(start: -24h) "
                    + "|> filter(fn: (r) => r._field == \"Price\" and r.symbol == \"btcusdt@trade\" ) "
                    + "|> aggregateWindow(every: 2s, fn: mean, createEmpty: false)";
            List<FluxTable> tables = client.getQueryApi().query(query, org);

            for (int i = 0; i < tables.size(); i++) {
                FluxTable table = tables.get(i);
                List<FluxRecord> records = table.getRecords();
                for (int j = 0; j < records.size(); j++) {
                    FluxRecord record = records.get(j);
                    System.out.println(record.getValues().toString());
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
