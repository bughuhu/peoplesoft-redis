package peoplesoft.redis.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ListPosition;

/**
 * PSedis
 * 
 * @author Administrator
 *
 */
public class PSedis {

	private static JedisPool jedisPool = null;
	private Jedis jedis;

	// 连接实例的最大连接数
	private static int MAX_ACTIVE = 1024;

	// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
	private static int MAX_IDLE = 200;

	// 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
	private static int MAX_WAIT = 10000;

	// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	private static boolean TEST_ON_BORROW = false;

	// 过期时间单位(秒)
	private final String EXPIRE_TIME_UNIT = "EX";

	/**
	 * 初始化连接
	 */
	public void init(String host, int port, String password, int timeOut) {

		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, host, port, timeOut, password);
		} catch (Exception e) {

		}

	}

	/**
	 * 从连接池中拿到一个实例
	 * 
	 * @return
	 */
	public void getPSedis() {
		try {
			if (jedisPool != null) {
				jedis = jedisPool.getResource();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	// Key（键），简单的key-value操作

	/**
	 * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
	 * 
	 * @param key
	 * @return
	 */
	public long ttl(String key) {
		return jedis.ttl(key);
	}

	/**
	 * 实现命令：expire 设置过期时间，单位秒
	 * 
	 * @param key
	 * @return
	 */
	public void expire(String key, int timeout) {
		jedis.expire(key, timeout);
	}

	/**
	 * 实现命令：INCR key，增加key一次，负数递减
	 * 
	 * @param key
	 * @return
	 */
	public long incr(String key, long delta) {
		return jedis.incrBy(key, delta);
	}

	/**
	 * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
	 */
	public Set<String> keys(String pattern) {
		return jedis.keys(pattern);
	}

	/**
	 * 实现命令：EXISTS key，判断key是否存在
	 * 
	 * @param key
	 * @return boolean
	 */
	public boolean hasKey(String key) {
		return jedis.exists(key);
	}

	/**
	 * 实现命令：DEL key，删除一个key
	 * 
	 * @param key
	 */
	public void del(String key) {
		jedis.del(key);
	}

	/**
	 * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		jedis.set(key, value);
	}

	/**
	 * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
	 * 
	 * @param key
	 * @param value
	 * @param timeout （以秒为单位）
	 */
	public void setx(String key, String value, long timeout) {
		jedis.set(key, value, EXPIRE_TIME_UNIT, timeout);
	}

	/**
	 * 实现命令：GET key，返回 key所关联的字符串值。
	 * 
	 * @param key
	 * @return value
	 */
	public String get(String key) {
		return (String) jedis.get(key);
	}

	// Hash（哈希表）

	/**
	 * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	public void hset(String key, String field, String value) {
		jedis.hset(key, field, value);
	}

	/**
	 * 实现命令：HMSET key map，将哈希表 key设置为多个键值得map
	 * 
	 * @param key
	 * @param map
	 */
	public void hmset(String key, Map<String, String> map) {
		jedis.hmset(key, map);
	}

	/**
	 * 实现命令：HMSET key map，将哈希表 key设置为多个键值得map,并设置超时时间
	 * 
	 * @param key
	 * @param map
	 * @param timeout
	 */
	public void hmsetx(String key, Map<String, String> map, int timeout) {
		jedis.hmset(key, map);

		if (timeout > 0) {
			jedis.expire(key, timeout);
		}

	}

	/**
	 * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field) {
		return (String) jedis.hget(key, field);
	}

	/**
	 * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 * 
	 * @param key
	 * @param fields
	 */
	public void hdel(String key, String... fields) {
		jedis.hdel(key, fields);
	}

	/**
	 * 实现命令：HINCR key，增加key一次，负数递减
	 * 
	 * @param key
	 * @param field
	 * @param dela
	 * @return
	 */
	public long hincr(String key, String field, long delta) {
		return jedis.hincrBy(key, field, delta);
	}

	/**
	 * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetall(String key) {
		return jedis.hgetAll(key);
	}

	/**
	 * 实现命令：HKEYS key 获取哈希表中的所有域（field）。
	 * 
	 * @return
	 */
	public Set<String> hkeys(String key) {
		return jedis.hkeys(key);
	}

	/**
	 * 实现命令：HVALS KEY_NAME FIELD VALUE 返回哈希表所有域(field)的值。
	 * 
	 * @param key
	 * @return
	 */
	public List<String> hvals(String key) {
		return jedis.hvals(key);
	}

	/**
	 * 实现命令：HLEN KEY_NAME 获取哈希表中字段的数量。
	 * 
	 * @param key
	 * @return
	 */
	public long hlen(String key) {
		return jedis.llen(key);
	}

	// List（列表）

	/**
	 * 实现命令：LINDEX KEY_NAME INDEX_POSITION 通过索引获取列表中的元素。你也可以使用负数下标，以 -1 表示列表的最后一个元素，
	 * -2 表示列表的倒数第二个元素，以此类推。
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public String lindex(String key, long index) {
		return jedis.lindex(key, index);
	}

	/**
	 * 实现命令：LINSERT key BEFORE|AFTER pivot value
	 * 用于在列表的元素前或者后插入元素。当指定元素不存在于列表中时，不执行任何操作。 当列表不存在时，被视为空列表，不执行任何操作。 如果 key
	 * 不是列表类型，返回一个错误。
	 * 
	 * @param key
	 * @param where
	 * @param pivot
	 * @param value
	 * @return
	 */
	public long linsert(String key, String where, String pivot, String value) {
		if (where.equalsIgnoreCase("after")) {
			return jedis.linsert(key, ListPosition.AFTER, pivot, value);
		} else {
			return jedis.linsert(key, ListPosition.BEFORE, pivot, value);
		}
	}

	/**
	 * 实现命令： LLEN KEY_NAME 用于返回列表的长度。 如果列表 key 不存在，则 key 被解释为一个空列表，返回 0 。 如果 key
	 * 不是列表类型，返回一个错误。
	 * 
	 * @param key
	 * @return
	 */
	public long llen(String key) {
		return jedis.llen(key);
	}

	/**
	 * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头,如果链表不存在 则创建
	 * 
	 * @param key
	 * @param value
	 * @return 执行 LPUSH命令后，列表的长度。
	 */
	public long lpush(String key, String value) {
		return jedis.lpush(key, value);
	}

	/**
	 * 实现命令：LPOP key，移除并返回列表 key的头元素。
	 * 
	 * @param key
	 * @return 列表key的头元素。
	 */
	public String lpop(String key) {
		return (String) jedis.lpop(key);
	}

	/**
	 * 实现命令：将一个值插入到已存在的列表头部，如果链表不存在 则报错;
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public long lpushx(String key, String... values) {
		return jedis.lpushx(key, values);
	}

	/**
	 * 实现命令：LRANGE KEY_NAME START END 返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定。 其中 0
	 * 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2
	 * 表示列表的倒数第二个元素，以此类推。
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	public List<String> lrange(String key, long start, long stop) {
		return jedis.lrange(key, start, stop);
	}

	/**
	 * LREM KEY_NAME COUNT VALUE 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。
	 * 
	 * COUNT 的值可以是以下几种：
	 * 
	 * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
	 * 
	 * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
	 * 
	 * count = 0 : 移除表中所有与 VALUE 相等的值。
	 * 
	 * @param key
	 * @param count
	 * @param value
	 * @return
	 */
	public long lrem(String key, long count, String value) {
		return jedis.lrem(key, count, value);
	}

	/**
	 * 实现命令： LSET KEY_NAME INDEX VALUE 通过索引来设置元素的值。
	 * 
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	public String lset(String key, long index, String value) {
		return jedis.lset(key, index, value);
	}

	/**
	 * 实现命令：LTRIM KEY_NAME START STOP
	 * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。 下标 0 表示列表的第一个元素，以 1
	 * 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	public String ltrim(String key, long start, long stop) {
		return jedis.ltrim(key, start, stop);
	}

	/**
	 * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
	 * 
	 * @param key
	 * @param value
	 * @return 执行 LPUSH命令后，列表的长度。
	 */
	public long rpush(String key, String value) {
		return jedis.rpush(key, value);
	}

	// Set 集合

	/**
	 * 实现命令：SADD key，向集合添加一个或多个成员
	 * 
	 * @param key
	 */
	public void sadd(String key, String... members) {
		jedis.sadd(key, members);
	}

	/**
	 * 实现命令：SADD key，添加值到集合key,并设置过期时间
	 * 
	 * @param key
	 */
	public void saddx(String key, int timeout, String... members) {
		jedis.sadd(key, members);
		if (timeout > 0) {
			jedis.expire(key, timeout);
		}
	}

	/**
	 * 实现命令：SCARD key 获取集合的成员数
	 * 
	 * @param key
	 * @return
	 */
	public long scard(String key) {
		return jedis.scard(key);
	}

	/**
	 * 实现命令：SDIFF keys... 命令返回给定集合之间的差集。不存在的集合 key 将视为空集
	 * 
	 * @param keys
	 * @return
	 */
	public Set<String> sdiff(String... keys) {
		return jedis.sdiff(keys);
	}

	/**
	 * 实现命令： SDIFFSTORE destKey keys 将给定集合之间的差集存储在指定的集合中。如果指定的集合 key 已存在，则会被覆盖。
	 * 
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long sdiffstore(String destKey, String... keys) {
		return jedis.sdiffstore(destKey, keys);
	}

	/**
	 * 实现命令：SISMEMBER key member 判断member是否存在于key集合中
	 * 
	 * @param key
	 * @param member
	 */
	public boolean sismember(String key, String member) {
		return jedis.sismember(key, member);
	}

	/**
	 * 实现命令：SMEMBERS key，获取key对应所有的集合值
	 * 
	 * @param key
	 */
	public Set<String> smembers(String key) {
		return jedis.smembers(key);
	}

	/**
	 * 实现命令：SMOVE source dest member 将指定成员 member元素从 source集合移动到 dest集合。
	 * 
	 * @param srckey
	 * @param dstkey
	 * @param member
	 * @return
	 */
	public long smove(String srckey, String dstkey, String member) {
		return jedis.smove(srckey, dstkey, member);
	}

	/**
	 * 实现命令：SPOP key 移除集合中的指定 key 的一个或多个随机元素，移除后会返回移除的元素。
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public Set<String> spop(String key, long count) {
		return jedis.spop(key, count);
	}

	/**
	 * 实现命令：SRANDMEMBER KEY [count] 返回集合中的一个或几个随机元素
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public List<String> srandmember(String key, int count) {
		return jedis.srandmember(key, count);
	}

	/**
	 * 实现命令：SUNION KEY KEY1..KEYN 返回给定集合的并集。不存在的集合 key 被视为空集。
	 * 
	 * @param keys
	 * @return
	 */
	public Set<String> sunion(String... keys) {
		return jedis.sunion(keys);
	}

	/**
	 * 实现命令：SUNIONSTORE DESTINATION KEY KEY1..KEYN 将给定集合的并集存储在指定的集合 destination 中。如果
	 * destination 已经存在，则将其覆盖。
	 * 
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long sunionstore(String destKey, String... keys) {
		return jedis.sunionstore(destKey, keys);
	}

	/**
	 * 实现命令：SUNION KEY KEY1..KEYN 返回给定集合的交集。不存在的集合 key 被视为空集。
	 * 
	 * @param keys
	 * @return
	 */
	public Set<String> sinter(String... keys) {
		return jedis.sinter(keys);
	}

	/**
	 * 实现命令：SINTERSTORE DESTINATION KEY KEY1..KEYN 将给定集合的交集存储在指定的集合 destination 中。如果
	 * destination 已经存在，则将其覆盖。
	 * 
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long sinterstore(String destKey, String... keys) {
		return jedis.sinterstore(destKey, keys);
	}

	/**
	 * 实现命令：SREM key members 移除集合中的值
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public long srem(String key, String... members) {
		return jedis.srem(key, members);
	}

	// ZSet 有序集合

	/**
	 * 
	 * @param key
	 * @param scoreMembers
	 * @return
	 */
	public long zadd(String key, Map<String, Double> scoreMembers) {
		return jedis.zadd(key, scoreMembers);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public long zcard(String key) {
		return jedis.zcard(key);
	}
	

	
}
