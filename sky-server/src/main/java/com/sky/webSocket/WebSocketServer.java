package com.sky.webSocket;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Collections;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket服务
 */
@Component
@Slf4j
@Transactional
@ServerEndpoint("/ws/{sid}")
// 常量定义优化（类顶部）
public class WebSocketServer {

    // 修改sessionMap为WeakHashMap
    private static final Map<String, Session> sessionMap = 
        Collections.synchronizedMap(new WeakHashMap<>());
    
    // 添加连接数限制
    // 改为枚举常量（新增）
    public enum SystemConstants {
        MAX_WS_CONNECTIONS(5000),
        MQ_CAPACITY(1000);
        
        private final int value;
        SystemConstants(int value) { this.value = value; }
        public int value() { return value; }
    }
    
    // 异常处理优化（onOpen方法）
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        if (sessionMap.size() >= SystemConstants.MAX_WS_CONNECTIONS.value()) {
// 首先需要定义自定义异常类
class ConnectionLimitExceededException extends RuntimeException {
    public ConnectionLimitExceededException(String message) {
        super(message);
    }
}
            throw new ConnectionLimitExceededException("连接数已达上限"); // 自定义异常
        }
        System.out.println("客户端：" + sid + "建立连接");
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        System.out.println("收到来自客户端：" + sid + "的信息:" + message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接断开:" + sid);
        sessionMap.remove(sid);
    }

    /**
     * 群发
     *
     * @param message
     */
    // 使用有界队列
    /**
     * 消息队列，用于存储待发送的消息。
     * 队列的容量为1000，即最多可以存储1000条消息。
     */
    // 新增消息队列（放在类变量区域）
    private static final LinkedBlockingQueue<String> MESSAGE_QUEUE = 
        new LinkedBlockingQueue<>(1000);

    // 增加异步发送逻辑（在sendToAllClient方法中）
    public void sendToAllClient(String message) {
        // 添加队列控制逻辑
        if (MESSAGE_QUEUE.remainingCapacity() == 0) {
            MESSAGE_QUEUE.poll(); // 队列满时移除最旧消息
        }
        MESSAGE_QUEUE.offer(message);
        
        // 原有消息发送逻辑保持不变
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sessions.parallelStream().forEach(session -> {
            try {
                session.getAsyncRemote().sendText(message); // 改为异步发送
            } catch (Exception e) {
                log.error("WebSocket消息发送失败", e);
            }
        });
    }
   
}


    

