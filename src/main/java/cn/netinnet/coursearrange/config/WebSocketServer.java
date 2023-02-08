package cn.netinnet.coursearrange.config;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cn.netinnet.coursearrange.domain.Message;
import cn.netinnet.coursearrange.entity.NinStudent;
import cn.netinnet.coursearrange.entity.NinTeacher;
import cn.netinnet.coursearrange.enums.UserTypeEnum;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@ServerEndpoint("/imserver/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId=userId;
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            webSocketMap.put(userId,this);
            //加入set中
        }else{
            webSocketMap.put(userId,this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }
        log.info("用户连接: "+userId+", 当前在线人数为:" + getOnlineCount());
        try {
            sendMessage(new Message(Long.parseLong(userId), "连接成功", 0));
        } catch (IOException | EncodeException e) {
            log.error("用户:"+userId+",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param msg 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String msg, Session session) {
        Message message = JSON.parseObject(msg, Message.class);
        System.out.println("---");
        System.out.println(message.getContent());
        System.out.println(message.getSendDate());

        //log.info("用户消息:"+userId+",报文:"+message);
        //可以群发消息
        //消息保存到数据库、redis
//        if(StringUtils.isNotBlank(message)){
//            try {
//                //解析发送的报文
//                JSONObject jsonObject = JSON.parseObject(message);
//                //追加发送人(防止串改)
//                jsonObject.put("fromUserId",this.userId);
//                String toUserId=jsonObject.getString("msg");
//                //传送给对应toUserId用户的websocket
//                if(StringUtils.isNotBlank(toUserId)&&webSocketMap.containsKey(toUserId)){
//                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
//                }else{
//                    log.error("请求的userId:"+toUserId+"不在该服务器上");
//                    //否则不在这个服务器上，发送到mysql或者redis
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
        error.printStackTrace();
    }

    Lock lock = new ReentrantLock();

    /**
     * 实现服务器主动推送
     */
    public synchronized void  sendMessage(Message message) throws IOException, EncodeException {
        try {
            lock.lock();
            this.session.getBasicRemote().sendText(JSON.toJSONString(message));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String content, @PathParam("userId") String userId) throws IOException, EncodeException {
        Message message = new Message();
        message.setUserId(Long.parseLong(userId));
        message.setSendDate(new Date());
        message.setContent(content);
        log.info("发送消息到:" + userId + ", 报文:" + content);
        if(StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(message);
        }else{
            log.error("用户" + userId + ",不在线！");
        }
    }

    /**
     * 给userIdList用户发送消息，如果为空则发送全部在线用户
     */
    public static void sendBatchInfo(String content, List<Long> userIdList, int code) throws IOException, EncodeException {
        log.info("发送消息到:全部, 报文:" + content);
        if (null == userIdList) {
            userIdList = webSocketMap.keySet().stream().map(Long::parseLong).collect(Collectors.toList());
        }
        for (Long userId : userIdList) {
            webSocketMap.get(userId.toString()).sendMessage(new Message(userId, content, code));
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}
