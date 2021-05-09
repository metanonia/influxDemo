package com.metanonia.influx.binance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.*;

public class MySqlApi {
    final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    final String DB_URL = "jdbc:mysql://dvlp.metanonia.com:3306/metanonia?useSSL=false"; // DB 접속 주소
    Connection conn = null;

    public MySqlApi(String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
        this.conn = DriverManager.getConnection(DB_URL, username, password);
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }

    public int insertData(String yhmhm, Double value, String curHash) {
        int ret = 0;
        try {
            String sql = "INSERT INTO binance (ymdhm, price, curHash) values (?,?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, yhmhm);
            pstmt.setDouble(2, value);
            pstmt.setString(3, curHash);

            ret = pstmt.executeUpdate();

            pstmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
