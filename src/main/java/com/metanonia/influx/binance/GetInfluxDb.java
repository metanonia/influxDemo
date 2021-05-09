package com.metanonia.influx.binance;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.util.List;

public class GetInfluxDb {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.exit(0);
        }
        String token = args[0];

        String bucket = "blockchain";
        String org = "metanonia";
        InfluxDBClient client = InfluxDBClientFactory.create("http://dvlp.metanonia.com:8086", token.toCharArray());

        try {
            String query = "from(bucket: \""+bucket+"\") |> range(start: 2021-04-25T14:48:00Z, stop: 2021-06-25T14:49:00Z ) "
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
