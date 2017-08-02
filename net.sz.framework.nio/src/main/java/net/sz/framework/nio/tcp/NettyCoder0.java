package net.sz.framework.nio.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.BitUtil;
import net.sz.framework.utils.MD5Util;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyCoder0 extends NettyCoder {

    private static final SzLogger log = SzLogger.getLogger();

    public NettyCoder0() {
    }

    public static byte[] AES_KEY = "vWf7g1Gt701h0.#0".getBytes();
    public static byte[] AES_IV = "rgnHV16#8HQFc&16".getBytes();

    /**
     * AES解密
     *
     * @author codingtony 2017年5月23日下午6:14:09
     * @param bytes
     * @return
     * @throws java.lang.Exception
     */
    protected static byte[] decryptAES(byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keyspec = new SecretKeySpec(AES_KEY, "AES");
        IvParameterSpec ivspec = new IvParameterSpec(AES_IV);
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] original = cipher.doFinal(bytes);
        return original;
    }

    /**
     * 澳博通信库
     *
     * @param b
     * @param timeStamp
     * @return
     */
    protected static String createSign(byte[] b, long timeStamp) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int c = b[i];
            if (c < 0) {
                c += 256;
            }
            sb.append(c);
        }
        sb.append(timeStamp);
        return MD5Util.md5Encode(sb.toString()).toUpperCase();
    }

    @Override
    protected boolean decode1(ChannelHandlerContext chc, ByteBuf buf) {
        try {
            int readableLen = buf.readableBytes();
            if (readableLen < 2) {
                return false;
            }
            buf.markReaderIndex();

            //1、packet length（c#客户端字节序为littleEnding，需要转换）
            short packet_len = BitUtil.readBytesToShortLittleEnding(buf);
            if (packet_len <= 0) {
                log.error("error......................packet len:" + packet_len);
                //正常情况不会进入
                buf.clear();
                chc.close();
                return false;
            }

            if (buf.readableBytes() < packet_len) {
                buf.resetReaderIndex();
                return false;
            }

            //2、mid（c#客户端字节序为littleEnding，需要转换）
            int mid = BitUtil.readBytesToIntLittleEnding(buf);

            //3、protobuf长度
            int protobuf_len = BitUtil.readBytesToIntLittleEnding(buf);

            //4、protobuf body
            byte[] bytes = new byte[protobuf_len];
            buf.readBytes(bytes, 0, protobuf_len);

            //5、检查消息签名
            long timeStamp = 0l;
            if (packet_len > (protobuf_len + 8)) {
                //客户端时间戳
                timeStamp = BitUtil.readBytesToLongLittleEnding(buf);

                //计算签名
                String sign1 = createSign(bytes, timeStamp);

                //解密签名数组
                int len_md5_data = BitUtil.readBytesToIntLittleEnding(buf);
                byte[] bytesMd5 = new byte[len_md5_data];
                buf.readBytes(bytesMd5, 0, len_md5_data);
                bytesMd5 = decryptAES(bytesMd5);

                //截取签名
                int len_clear_sign = BitUtil.readBytesToIntLittleEnding(buf);
                byte[] clearSignBytes = new byte[len_clear_sign];
                System.arraycopy(bytesMd5, 0, clearSignBytes, 0, len_clear_sign);
                String sign2 = new String(clearSignBytes, "utf-8");

                //检查签名是否一致
                if (!sign1.equals(sign2)) {
                    log.info("---------------------------签名验证失败!" + Arrays.toString(bytes));
                    chc.close();
                    return false;
                }
            }
            /*处理消息--理论上是丢出去了的*/
            actionMessage(chc, mid, bytes);
        } catch (Exception e) {
            //如果解包出错，清空本次数据，让下次请求顺利通过
            buf.clear();
            log.info("澳博解包体系", e);
        }
        return false;
    }

    @Override
    public ByteBuf getByteBufFormBytes(int mid, byte[] bytes) {

        int pack_len = 2 + 4 + 4;
        if (bytes != null) {
            pack_len += bytes.length;
        }

        ByteBuf buf = Unpooled.buffer(pack_len);
        /*书写包长度*/
        buf.writeByte((short) pack_len);
        /*写入mid*/
        byte[] bytesMid = BitUtil.writeIntToBytesLittleEnding(mid);
        buf.writeBytes(bytesMid);

        if (bytes != null && bytes.length > 0) {
            /*写入protobuf len(客户端lua处理protobuf时需要)*/
            byte[] bytesLen = BitUtil.int2Bytes(bytes.length, ByteOrder.LITTLE_ENDIAN);
            buf.writeBytes(bytesLen);
            /*写入protobuf body*/
            buf.writeBytes(bytes);
        }
        return buf;
    }

}
