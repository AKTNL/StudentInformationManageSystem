package com.student.studentmanagesystembackend.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServer {
    // 静态变量，用来记录当前在线连接数
    private static int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的 WebSocketServer 对象
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接受 userId
    private String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){
        this.session = session;
        this.userId = userId;
        webSocketSet.add(this); // 加入set中
        addOnlineCount(); // 在线数加1
        System.out.println("用户" + userId + "加入连接！当前在线人数为" + getOnlineCount());
    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this); // 从set中删除
        subOnlineCount(); // 在线数减1
        System.out.println("用户" + userId + "关闭连接！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("来自客户端" + userId + "的消息:" + message);
    }

    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("WebSocket发生错误");
        error.printStackTrace();
    }

    public static void sendInfo(String message){
        for(WebSocketServer item : webSocketSet){
            try{
                item.sendMessage(message);
            }catch(IOException e){
                continue;
            }
        }
    }

    //发送消息
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount(){
        return onlineCount;
    }

    public static synchronized void addOnlineCount(){
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount(){
        WebSocketServer.onlineCount--;
    }
}
