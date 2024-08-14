package com.example.core;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.example.dao.TestDao;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LockExample {

  //1、创建锁对象
  private final Lock lock = new ReentrantLock();
  
  //2、使用锁对象进行同步
  public void synchronizedMethod() throws Exception{
    lock.lock(); // 获取锁
    try {
      // 同步代码块 
      // ...
      new TestDao().TestFindById();

      System.out.println("锁住执行代码块");

    } finally {
      lock.unlock(); // 释放锁
    }
  }
  public static void main(String[] args) throws Exception{
    String res = new Pager("user",1,200000).pagingForSingleTable();
    new LockExample().synchronizedMethod();
  }
}