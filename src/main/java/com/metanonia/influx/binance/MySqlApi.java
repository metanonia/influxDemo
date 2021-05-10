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

    public int insertData(String yhmhm, Double value, String curHash, String sumHash) {
        int ret = 0;
        try {
            String sql = "INSERT INTO binance (ymdhm, price, curHash, sumHash) values (?,?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, yhmhm);
            pstmt.setDouble(2, value);
            pstmt.setString(3, curHash);
            pstmt.setString(4, sumHash);

            ret = pstmt.executeUpdate();

            pstmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String getLastSumHash() {
        String ret = null;
        try {
            String sql = "SELECT sumHash FROM binance ORDER BY seqno DESC LIMIT 1";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next() == false) return null;
            else {
                return resultSet.getString(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
