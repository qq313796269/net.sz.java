package net.sz.framework.nio.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestClient {

    private static final SzLogger log = SzLogger.getLogger();
    static NettyTcpClient client = null;

    public static void main(String[] args) {

        client = new NettyTcpClient("127.0.0.1", 9527, NettyCoder.getDefaultCoder(), new INettyHandler() {

        });

        Channel connect = client.connect();

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
