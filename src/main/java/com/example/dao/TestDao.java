package com.example.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.example.core.JdbcExample;

public class TestDao {

  public void testFindById(){
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try {
        connection = JdbcExample.getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select * from users where u_id='2'");

        if (resultSet.next()){
          Integer id = resultSet.getInt("u_id");
          String name = resultSet.getString("u_name");
          System.out.println(id + " " + name);
        }

    } catch (SQLException throwables) {
        throwables.printStackTrace();
    } finally {
        JdbcExample.closeResource(connection,statement,resultSet);
    }
  }
  public static void main(String[] args) {
    new TestDao().testFindById();
  }
}
