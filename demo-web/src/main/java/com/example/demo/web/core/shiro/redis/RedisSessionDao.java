package com.example.demo.web.core.shiro.redis;

import com.example.demo.web.core.properties.SysProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * SessionDao自定义实现
 *
 * @author: eric
 * @date: 2018年10月19日
 */
@Component
@Slf4j
public class RedisSessionDao extends AbstractSessionDAO {

    private final String PREFIX = "shiro_redis_session:";

    @Resource(name = "redisTemplate_shiro")
    private RedisTemplate redisTemplate;
    @Autowired
    private SysProperties sysProperties;

    @Override
    public void update(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            log.error("redis update session error:session or session id is null");
            return;
        }
        log.debug("更新seesion,id=[{}]", session.getId().toString());
        try {
            redisTemplate.opsForValue().set(PREFIX + session.getId().toString(), session, sysProperties.getSessionInvalidateTime(), TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new UnknownSessionException(e);
        }
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("redis delete session error:session or session id is null");
            return;
        }
        log.debug("删除seesion,id=[{}]", session.getId().toString());
        try {
            redisTemplate.delete(PREFIX + session.getId().toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return (Collection<Session>) redisTemplate.execute(new RedisCallback<Collection<Session>>() {
            @Override
            public Collection<Session> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<Session> sessions = new HashSet<Session>();
                Set keys = redisTemplate.keys(PREFIX + "*");

                for (Object key : keys) {
                    Session session = (Session) redisTemplate.opsForValue().get(key);
                    sessions.add(session);
                }
                return sessions;
            }
        });
    }

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            log.error("redis create session error:session  is null");
            return null;
        }
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        log.debug("创建seesion,id=[{}]", session.getId().toString());
        redisTemplate.opsForValue().set(PREFIX + sessionId.toString(), session, sysProperties.getSessionInvalidateTime(), TimeUnit.MINUTES);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.error("redis read session error:sessionId is null");
            return null;
        }
        log.debug("获取seesion,id=[{}]", sessionId.toString());
        Session session = null;
        try {
            session = (Session) redisTemplate.opsForValue().get(PREFIX + sessionId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return session;
    }
}
