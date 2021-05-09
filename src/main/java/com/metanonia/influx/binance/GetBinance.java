package com.metanonia.influx.binance;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

public class GetBinance extends WebSocketClient {
    static String token = null;
    String bucket = "blockchain";
    String org = "metanonia";
    static InfluxDBClient client;

    public GetBinance(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");

    }

    @Override
    public void onMessage(String message) {
        //System.out.println("received: " + message);
        JSONObject jObj = new JSONObject(message);
        Point point = Point
                .measurement("binance")
                .addTag("symbol", "btcusdt@trade")
                .addField("TradeId", jObj.getBigDecimal("t"))
                .addField("Price", jObj.getBigDecimal("p"))
                .addField("Quantity", jObj.getBigDecimal("q"))
                .addField("BuyerOrderId", jObj.getBigDecimal("b"))
                .addField("SellerOrderId", jObj.getBigDecimal("a"))
                .addField("TradeTime", jObj.getBigDecimal("T"))
                .addField("IsBuyerMake", jObj.getBoolean("m"))
                .time(Instant.now(), WritePrecision.NS);


        try (WriteApi writeApi = client.getWriteApi()) {
            writeApi.writePoint(bucket, org, point);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args)  {
        if(args.length != 1) {
            System.exit(0);
        }
        token = args[0];

        try {
            client = InfluxDBClientFactory.create("http://dvlp.metanonia.com:8086", token.toCharArray());
            GetBinance binance = new GetBinance(new URI("wss://stream.binance.com:9443/ws/btcusdt@trade"));
            binance.connect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
