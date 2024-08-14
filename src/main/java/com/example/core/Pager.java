package com.example.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页类
 * JdbcExample 自主封装JDBC 可以平替其他API
 * ...后续有待扩展(eg:排序、动态条件、多线程查询...)
 * 只适应单表查询
 */
public class Pager {
  private String tableName; // 表名
  private Integer pageNumber; // 当前页码
  private Integer pageSize; // 每页显示条数
  private Connection connection = null; // 数据库连接对象
  private Statement statement = null;// 发送sql语句(不带参数)
  private PreparedStatement preparedStatement = null; // 发送sql语句(带参数)
  private ResultSet resultSet = null; //查询返回的结果集合
  Logger log = LoggerFactory.getLogger(Pager.class);
  private String queryStr; // 如果是多表，传入查询语句()
  public Pager() {

  }

  //单表使用的构造方法
  public Pager(String tableName, Integer pageNumber, Integer pageSize) {
    this.tableName = tableName;
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }

  //多表使用重载的构造方法
  public Pager(Integer pageNumber, Integer pageSize,String queryStr) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.queryStr = queryStr;
  }

  public String getQueryStr() {
    return queryStr;
  }

  public void setQueryStr(String queryStr) {
    this.queryStr = queryStr;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * 获取总条数
   * @return total
   */
  private int getTotal() {
    int total = 0;
    String sql = "select count(*) from " + this.getTableName() + " where 1=1";
    try {
      this.connection = JdbcExample.getConnection();
      this.statement = this.connection.createStatement();
      this.resultSet = this.statement.executeQuery(sql);
      if (this.resultSet.next()) {
        total = this.resultSet.getInt(1);
      }

      this.log.info("total:->{}",sql);
    } catch (Exception e) {
      // TODO: handle exception
      this.log.info(e.getMessage());
    } finally {
      JdbcExample.closeResource(this.connection, this.statement, this.resultSet);
    }
    return total;
  }

  /**
   * 根据分页参数获取对应数据
   * @return list
   */
  private List<Map<String, Object>> getList() {
    List<Map<String, Object>> data = new ArrayList<>();
    int offset = (this.getPageNumber() - 1) * this.getPageSize();
    String sql = "select * from " + this.getTableName() + " where 1=1 limit ?,?";
    try {
      this.connection = JdbcExample.getConnection();
      this.statement = this.connection.createStatement();
      this.preparedStatement = this.connection.prepareStatement(sql);
      this.preparedStatement.setInt(1, offset);
      this.preparedStatement.setInt(2, this.getPageSize());
      resultSet = preparedStatement.executeQuery();
      data = this.resultSetToList(this.resultSet);
      this.log.info("getList:->{}",sql+"---param:"+offset+","+this.getPageSize());
    } catch (Exception e) {
      this.log.info(e.getMessage());
    } finally {
      JdbcExample.closeResource(this.connection, this.statement, this.resultSet);
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

  /**
   * 通用分页处理(单表)
   * @return String
   */
  public String pagingForSingleTable() throws JsonProcessingException {
    Map<String, Object> res = new HashMap<>();
    res.put("total", this.getTotal());
    ObjectMapper mapper = new ObjectMapper();
    // 将Map转换为JSON字符串
    res.put("queryResult",this.getList());
    String str = mapper.writeValueAsString(res);
    this.log.info("res->{}",str);
    return str;
  }

  /**
   * 通用分页处理(多表)
   */
  void pagingForMoreTable(){
    String moreTableSql = this.queryStr+"limit ?,?";
  }
}
