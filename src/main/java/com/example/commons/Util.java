package com.example.commons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.core.JdbcExample;
import com.example.core.Pager;

public class Util implements Base {

  private Connection connection = null;
  private Statement statement = null;
  private ResultSet resultSet = null;

  public static void publicMethod() {
    // ...
    System.out.println("publicMethod");
  }

  @Override
  public void add() {
    // TODO Auto-generated method stub
    System.out.println("313212");
  }

  /**
   * @desc 通用分页处理
   * @param pager
   * @return Map<String, Object>
   */
  public Map<String, Object> pagingCore(Pager pager) {
    Map<String, Object> res = new HashMap<>();
    // 1、计算总条数
    res.put("total", this.getTotal(pager.getTableName()));

    // 2、查询分页数据
    res.put("data", this.getList(pager));
    return res;
  }

  /**
   * @Desc 获取总条数
   * @param tableName
   * @return total
   */
  private int getTotal(String tableName) {
    int total = 0;
    String sql = "select count(*) from " + tableName + " where 1=1";
    System.out.println("total------>>>>"+sql);
    try {
      connection = JdbcExample.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(sql);
      if (resultSet.next()) {
        total = resultSet.getInt(1);
      }

    } catch (Exception e) {
      // TODO: handle exception
    } finally {
      JdbcExample.closeResource(connection, statement, resultSet);
    }
    return total;
  }

  private List<Map<String, Object>> getList(Pager pager) {
    List<Map<String, Object>> data = new ArrayList<>();
    int offset = (pager.getPageNumber() - 1) * pager.getPageSize();
    String queryData = "select * from " + pager.getTableName() + " where 1=1 limit ?,?";
    System.out.println("queryData------>>>>"+queryData);
    try {
      connection = JdbcExample.getConnection();
      statement = connection.createStatement();
      PreparedStatement pstmt = connection.prepareStatement(queryData);
      pstmt.setInt(1, offset);
      pstmt.setInt(2, pager.getPageSize());
      resultSet = pstmt.executeQuery();
      data = this.resultSetToList(resultSet);

    } catch (Exception e) {

    } finally {
      JdbcExample.closeResource(connection, statement, resultSet);
    }
    return data;
  }

  /**
   * jdbc结果集转换为列表
   * @param rs
   * @return List
   * @throws SQLException
   */
  private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
    List<Map<String, Object>> list = new ArrayList<>();
    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    while (rs.next()) {
      Map<String, Object> row = new HashMap<>();
      for (int i = 1; i <= columnCount; i++) {
        row.put(metaData.getColumnName(i), rs.getObject(i));
      }
      list.add(row);
    }

    return list;
  }
}
