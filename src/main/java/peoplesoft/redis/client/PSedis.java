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

	// ����ʵ�������������
	private static int MAX_ACTIVE = 1024;

	// ����һ��pool����ж��ٸ�״̬Ϊidle(���е�)��jedisʵ����Ĭ��ֵҲ��8��
	private static int MAX_IDLE = 200;

	// �ȴ��������ӵ����ʱ�䣬��λ���룬Ĭ��ֵΪ-1����ʾ������ʱ����������ȴ�ʱ�䣬��ֱ���׳�JedisConnectionException
	private static int MAX_WAIT = 10000;

	// ��borrowһ��jedisʵ��ʱ���Ƿ���ǰ����validate���������Ϊtrue����õ���jedisʵ�����ǿ��õģ�
	private static boolean TEST_ON_BORROW = false;

	// ����ʱ�䵥λ(��)
	private final String EXPIRE_TIME_UNIT = "EX";

	/**
	 * ��ʼ������
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
	 * �����ӳ����õ�һ��ʵ��
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

	// Key���������򵥵�key-value����

	/**
	 * ʵ�����TTL key������Ϊ��λ�����ظ��� key��ʣ������ʱ��(TTL, time to live)��
	 * 
	 * @param key
	 * @return
	 */
	public long ttl(String key) {
		return jedis.ttl(key);
	}

	/**
	 * ʵ�����expire ���ù���ʱ�䣬��λ��
	 * 
	 * @param key
	 * @return
	 */
	public void expire(String key, int timeout) {
		jedis.expire(key, timeout);
	}

	/**
	 * ʵ�����INCR key������keyһ�Σ������ݼ�
	 * 
	 * @param key
	 * @return
	 */
	public long incr(String key, long delta) {
		return jedis.incrBy(key, delta);
	}

	/**
	 * ʵ�����KEYS pattern���������з��ϸ���ģʽ pattern�� key
	 */
	public Set<String> keys(String pattern) {
		return jedis.keys(pattern);
	}

	/**
	 * ʵ�����EXISTS key���ж�key�Ƿ����
	 * 
	 * @param key
	 * @return boolean
	 */
	public boolean hasKey(String key) {
		return jedis.exists(key);
	}

	/**
	 * ʵ�����DEL key��ɾ��һ��key
	 * 
	 * @param key
	 */
	public void del(String key) {
		jedis.del(key);
	}

	/**
	 * ʵ�����SET key value������һ��key-value�����ַ���ֵ value������ key��
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		jedis.set(key, value);
	}

	/**
	 * ʵ�����SET key value EX seconds������key-value�ͳ�ʱʱ�䣨�룩
	 * 
	 * @param key
	 * @param value
	 * @param timeout ������Ϊ��λ��
	 */
	public void setx(String key, String value, long timeout) {
		jedis.set(key, value, EXPIRE_TIME_UNIT, timeout);
	}

	/**
	 * ʵ�����GET key������ key���������ַ���ֵ��
	 * 
	 * @param key
	 * @return value
	 */
	public String get(String key) {
		return (String) jedis.get(key);
	}

	// Hash����ϣ��

	/**
	 * ʵ�����HSET key field value������ϣ�� key�е��� field��ֵ��Ϊ value
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	public void hset(String key, String field, String value) {
		jedis.hset(key, field, value);
	}

	/**
	 * ʵ�����HMSET key map������ϣ�� key����Ϊ�����ֵ��map
	 * 
	 * @param key
	 * @param map
	 */
	public void hmset(String key, Map<String, String> map) {
		jedis.hmset(key, map);
	}

	/**
	 * ʵ�����HMSET key map������ϣ�� key����Ϊ�����ֵ��map,�����ó�ʱʱ��
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
	 * ʵ�����HGET key field�����ع�ϣ�� key�и����� field��ֵ
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public String hget(String key, String field) {
		return (String) jedis.hget(key, field);
	}

	/**
	 * ʵ�����HDEL key field [field ...]��ɾ����ϣ�� key �е�һ������ָ���򣬲����ڵ��򽫱����ԡ�
	 * 
	 * @param key
	 * @param fields
	 */
	public void hdel(String key, String... fields) {
		jedis.hdel(key, fields);
	}

	/**
	 * ʵ�����HINCR key������keyһ�Σ������ݼ�
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
	 * ʵ�����HGETALL key�����ع�ϣ�� key�У����е����ֵ��
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetall(String key) {
		return jedis.hgetAll(key);
	}

	/**
	 * ʵ�����HKEYS key ��ȡ��ϣ���е�������field����
	 * 
	 * @return
	 */
	public Set<String> hkeys(String key) {
		return jedis.hkeys(key);
	}

	/**
	 * ʵ�����HVALS KEY_NAME FIELD VALUE ���ع�ϣ��������(field)��ֵ��
	 * 
	 * @param key
	 * @return
	 */
	public List<String> hvals(String key) {
		return jedis.hvals(key);
	}

	/**
	 * ʵ�����HLEN KEY_NAME ��ȡ��ϣ�����ֶε�������
	 * 
	 * @param key
	 * @return
	 */
	public long hlen(String key) {
		return jedis.llen(key);
	}

	// List���б�

	/**
	 * ʵ�����LINDEX KEY_NAME INDEX_POSITION ͨ��������ȡ�б��е�Ԫ�ء���Ҳ����ʹ�ø����±꣬�� -1 ��ʾ�б�����һ��Ԫ�أ�
	 * -2 ��ʾ�б�ĵ����ڶ���Ԫ�أ��Դ����ơ�
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public String lindex(String key, long index) {
		return jedis.lindex(key, index);
	}

	/**
	 * ʵ�����LINSERT key BEFORE|AFTER pivot value
	 * �������б��Ԫ��ǰ���ߺ����Ԫ�ء���ָ��Ԫ�ز��������б���ʱ����ִ���κβ����� ���б�����ʱ������Ϊ���б���ִ���κβ����� ��� key
	 * �����б����ͣ�����һ������
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
	 * ʵ����� LLEN KEY_NAME ���ڷ����б�ĳ��ȡ� ����б� key �����ڣ��� key ������Ϊһ�����б����� 0 �� ��� key
	 * �����б����ͣ�����һ������
	 * 
	 * @param key
	 * @return
	 */
	public long llen(String key) {
		return jedis.llen(key);
	}

	/**
	 * ʵ�����LPUSH key value����һ��ֵ value���뵽�б� key�ı�ͷ,����������� �򴴽�
	 * 
	 * @param key
	 * @param value
	 * @return ִ�� LPUSH������б�ĳ��ȡ�
	 */
	public long lpush(String key, String value) {
		return jedis.lpush(key, value);
	}

	/**
	 * ʵ�����LPOP key���Ƴ��������б� key��ͷԪ�ء�
	 * 
	 * @param key
	 * @return �б�key��ͷԪ�ء�
	 */
	public String lpop(String key) {
		return (String) jedis.lpop(key);
	}

	/**
	 * ʵ�������һ��ֵ���뵽�Ѵ��ڵ��б�ͷ��������������� �򱨴�;
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public long lpushx(String key, String... values) {
		return jedis.lpushx(key, values);
	}

	/**
	 * ʵ�����LRANGE KEY_NAME START END �����б���ָ�������ڵ�Ԫ�أ�������ƫ���� START �� END ָ���� ���� 0
	 * ��ʾ�б�ĵ�һ��Ԫ�أ� 1 ��ʾ�б�ĵڶ���Ԫ�أ��Դ����ơ� ��Ҳ����ʹ�ø����±꣬�� -1 ��ʾ�б�����һ��Ԫ�أ� -2
	 * ��ʾ�б�ĵ����ڶ���Ԫ�أ��Դ����ơ�
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
	 * LREM KEY_NAME COUNT VALUE ���ݲ��� COUNT ��ֵ���Ƴ��б�������� VALUE ��ȵ�Ԫ�ء�
	 * 
	 * COUNT ��ֵ���������¼��֣�
	 * 
	 * count > 0 : �ӱ�ͷ��ʼ���β�������Ƴ��� VALUE ��ȵ�Ԫ�أ�����Ϊ COUNT ��
	 * 
	 * count < 0 : �ӱ�β��ʼ���ͷ�������Ƴ��� VALUE ��ȵ�Ԫ�أ�����Ϊ COUNT �ľ���ֵ��
	 * 
	 * count = 0 : �Ƴ����������� VALUE ��ȵ�ֵ��
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
	 * ʵ����� LSET KEY_NAME INDEX VALUE ͨ������������Ԫ�ص�ֵ��
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
	 * ʵ�����LTRIM KEY_NAME START STOP
	 * ��һ���б�����޼�(trim)������˵�����б�ֻ����ָ�������ڵ�Ԫ�أ�����ָ������֮�ڵ�Ԫ�ض�����ɾ���� �±� 0 ��ʾ�б�ĵ�һ��Ԫ�أ��� 1
	 * ��ʾ�б�ĵڶ���Ԫ�أ��Դ����ơ� ��Ҳ����ʹ�ø����±꣬�� -1 ��ʾ�б�����һ��Ԫ�أ� -2 ��ʾ�б�ĵ����ڶ���Ԫ�أ��Դ����ơ�
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
	 * ʵ�����RPUSH key value����һ��ֵ value���뵽�б� key�ı�β(���ұ�)��
	 * 
	 * @param key
	 * @param value
	 * @return ִ�� LPUSH������б�ĳ��ȡ�
	 */
	public long rpush(String key, String value) {
		return jedis.rpush(key, value);
	}

	// Set ����

	/**
	 * ʵ�����SADD key���򼯺����һ��������Ա
	 * 
	 * @param key
	 */
	public void sadd(String key, String... members) {
		jedis.sadd(key, members);
	}

	/**
	 * ʵ�����SADD key�����ֵ������key,�����ù���ʱ��
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
	 * ʵ�����SCARD key ��ȡ���ϵĳ�Ա��
	 * 
	 * @param key
	 * @return
	 */
	public long scard(String key) {
		return jedis.scard(key);
	}

	/**
	 * ʵ�����SDIFF keys... ����ظ�������֮��Ĳ�������ڵļ��� key ����Ϊ�ռ�
	 * 
	 * @param keys
	 * @return
	 */
	public Set<String> sdiff(String... keys) {
		return jedis.sdiff(keys);
	}

	/**
	 * ʵ����� SDIFFSTORE destKey keys ����������֮��Ĳ�洢��ָ���ļ����С����ָ���ļ��� key �Ѵ��ڣ���ᱻ���ǡ�
	 * 
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long sdiffstore(String destKey, String... keys) {
		return jedis.sdiffstore(destKey, keys);
	}

	/**
	 * ʵ�����SISMEMBER key member �ж�member�Ƿ������key������
	 * 
	 * @param key
	 * @param member
	 */
	public boolean sismember(String key, String member) {
		return jedis.sismember(key, member);
	}

	/**
	 * ʵ�����SMEMBERS key����ȡkey��Ӧ���еļ���ֵ
	 * 
	 * @param key
	 */
	public Set<String> smembers(String key) {
		return jedis.smembers(key);
	}

	/**
	 * ʵ�����SMOVE source dest member ��ָ����Ա memberԪ�ش� source�����ƶ��� dest���ϡ�
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
	 * ʵ�����SPOP key �Ƴ������е�ָ�� key ��һ���������Ԫ�أ��Ƴ���᷵���Ƴ���Ԫ�ء�
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public Set<String> spop(String key, long count) {
		return jedis.spop(key, count);
	}

	/**
	 * ʵ�����SRANDMEMBER KEY [count] ���ؼ����е�һ���򼸸����Ԫ��
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public List<String> srandmember(String key, int count) {
		return jedis.srandmember(key, count);
	}

	/**
	 * ʵ�����SUNION KEY KEY1..KEYN ���ظ������ϵĲ����������ڵļ��� key ����Ϊ�ռ���
	 * 
	 * @param keys
	 * @return
	 */
	public Set<String> sunion(String... keys) {
		return jedis.sunion(keys);
	}

	/**
	 * ʵ�����SUNIONSTORE DESTINATION KEY KEY1..KEYN ���������ϵĲ����洢��ָ���ļ��� destination �С����
	 * destination �Ѿ����ڣ����串�ǡ�
	 * 
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long sunionstore(String destKey, String... keys) {
		return jedis.sunionstore(destKey, keys);
	}

	/**
	 * ʵ�����SUNION KEY KEY1..KEYN ���ظ������ϵĽ����������ڵļ��� key ����Ϊ�ռ���
	 * 
	 * @param keys
	 * @return
	 */
	public Set<String> sinter(String... keys) {
		return jedis.sinter(keys);
	}

	/**
	 * ʵ�����SINTERSTORE DESTINATION KEY KEY1..KEYN ���������ϵĽ����洢��ָ���ļ��� destination �С����
	 * destination �Ѿ����ڣ����串�ǡ�
	 * 
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long sinterstore(String destKey, String... keys) {
		return jedis.sinterstore(destKey, keys);
	}

	/**
	 * ʵ�����SREM key members �Ƴ������е�ֵ
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public long srem(String key, String... members) {
		return jedis.srem(key, members);
	}

	// ZSet ���򼯺�

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
