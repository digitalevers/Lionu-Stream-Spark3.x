package com.digitalevers

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object redisUtil {
  // 配置连接池
  private val poolConfig = new JedisPoolConfig()
  poolConfig.setMaxTotal(128) // 最大连接数
  poolConfig.setMaxIdle(128)  // 最大空闲连接数
  poolConfig.setMinIdle(16)   // 最小空闲连接数

  // Jedis连接池实例
  private[this] var jedisPool: JedisPool = _

  /**
   * 获取 Jedis 连接资源实例
   * @param host      Redis服务器地址
   * @param port      Redis服务器端口
   * @param timeout   连接超时时间 单位ms 默认20ms 0则不设置超时时间
   * @param password  连接密码 默认 null
   * @param database  连接数据库序号 默认0号
   */
  def getJedisRes(host: String = "localhost", port: Int = 6379, timeout: Int = 0, database: Int = 0, password: String = null): Jedis = {
    if (jedisPool == null) {
      synchronized {
        if (jedisPool == null) {
          jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database)
        }
      }
    }
    jedisPool.getResource
  }

  // 关闭连接池
  def closePool(): Unit = jedisPool.close()
}

object RedisExample {
  def main(args: Array[String]): Unit = {
    // 从连接池中获取一个Jedis实例
    val jedis = redisUtil.getJedisRes()

    try {
      // 设置键值对
      jedis.set("foo", "bar")

      // 获取键值对
      val value = jedis.get("foo")
      println(s"Value of 'foo': $value")
    } finally {
      // 归还连接到连接池
      jedis.close()
    }

    // 关闭连接池（在应用程序关闭时调用）
    //redisUtil.closePool()
  }
}