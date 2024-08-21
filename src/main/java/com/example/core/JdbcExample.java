package com.example.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcExample {

  private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/testing?useUnicode=true&characterEncoding=utf8";
    private static String user = "root";
    private static String password = "Zhoudf,,368878";

    static {
        try {
            //注册驱动
            Class.forName(driver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        //获得连接
        return DriverManager.getConnection(url, user, password);
    }

    /** 释放资源
     * @param conn
     * @param st
     * @param rs
     */
    public static void closeResource(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
}