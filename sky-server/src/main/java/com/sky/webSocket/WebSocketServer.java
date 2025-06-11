package com.sky.webSocket;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Collections;


/**
 * WebSocket服务
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{sid}")
// 常量定义优化（类顶部）
public class WebSocketServer {

    // 存放会话对象，使用线程安全的ConcurrentHashMap
    private static final Map<String, Session> sessionMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * 消息队列，用于存储待发送的消息。
     * 队列的容量为1000，即最多可以存储1000条消息。
     */
    private static final LinkedBlockingQueue<String> MESSAGE_QUEUE = new LinkedBlockingQueue<>(1000);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
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
     * 群发消息
     *
     * @param message 消息内容
     */
    public void sendToAllClient(String message) {
        // 添加队列控制逻辑
        if (MESSAGE_QUEUE.remainingCapacity() == 0) {
            MESSAGE_QUEUE.poll(); // 队列满时移除最旧消息
        }
        MESSAGE_QUEUE.offer(message);

        // 异步发送消息
        Collection<Session> sessions = sessionMap.values();
        sessions.parallelStream().forEach(session -> {
            try {
                session.getAsyncRemote().sendText(message); // 使用异步发送
            } catch (Exception e) {
                log.error("WebSocket消息发送失败", e);
            }
        });
    }
}
