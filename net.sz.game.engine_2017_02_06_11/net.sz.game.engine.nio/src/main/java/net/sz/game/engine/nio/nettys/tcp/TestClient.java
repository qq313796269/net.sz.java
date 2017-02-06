package net.sz.game.engine.nio.nettys.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestClient {

    static final Logger log = Logger.getLogger(TestClient.class);
    static NettyTcpClient client = null;

    public static void main(String[] args) {

        client = new NettyTcpClient(new INettyHandler() {

            @Override
            public void channelActive(String channelID, ChannelHandlerContext session) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void closeSession(String channelID, ChannelHandlerContext session) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void channelInactive(String channelID, ChannelHandlerContext session) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });

        Channel connect = client.connect("127.0.0.1", 9527);

//        BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
//        while (true) {
//            try {
//                String str = strin.readLine();
//                //构建聊天消息
//                TestMessage.ReqChatMessage.Builder chatmessage = TestMessage.ReqChatMessage.newBuilder();
//                chatmessage.setMsg(str);
//                TestClient.client.sendMsg(new NettyMessageBean(TestMessage.Proto_Login.ReqChat_VALUE, chatmessage.build().toByteArray()));
//            } catch (IOException ex) {
//            }
//        }
    }

}
