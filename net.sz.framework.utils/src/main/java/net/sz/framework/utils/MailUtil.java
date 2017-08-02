package net.sz.framework.utils;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.sz.framework.szlog.SzLogger;

/**
 * 用于程序向研发人员发送邮件
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MailUtil {

    private static final SzLogger log = SzLogger.getLogger();
    static final String SMTP_STRING = "smtp.exmail.qq.com";
    static final String username = "dyfservermail@dyfgame.com";
    static final String password = "Dyf1qaz2wsx";
    static final String to = "492794628@qq.com";

    static final class MialTask implements Runnable {

        String title;
        String context;

        public MialTask(String title, String context) {
            this.title = title;
            this.context = context;
        }

        @Override
        public void run() {
            long bigen = TimeUtil.currentTimeMillis();
            for (int i = 0; i < 5; i++) {
                try {
                    Properties props = new Properties(); //可以加载一个配置文件
                    // 使用smtp：简单邮件传输协议
                    props.put("mail.smtp.ssl.enable", "false");
                    props.put("mail.smtp.host", SMTP_STRING);//存储发送邮件服务器的信息
                    props.put("mail.smtp.auth", "true");//同时通过验证

                    Session session = Session.getInstance(props);//根据属性新建一个邮件会话
                    //        session.setDebug(true); //有他会打印一些调试信息。
                    MimeMessage message = new MimeMessage(session);//由邮件会话新建一个消息对象

                    message.setFrom(new InternetAddress(username));//设置发件人的地址
                    String[] split = to.split(";|；");
                    for (String string : split) {
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(string));
                    }
                    //设置标题
                    message.setSubject(title);
                    //设置信件内容
                    message.setText(context, "utf-8"); //发送文本文件
                    //message.setContent(context, "text/html;charset=utf-8"); //发送HTML邮件，内容样式比较丰富
                    message.setSentDate(new Date());//设置发信时间
                    message.saveChanges();//存储邮件信息
                    //发送邮件
                    Transport transport = null;
                    try {
                        transport = session.getTransport("smtp");
                        transport.connect(username, password);
                        //发送邮件,其中第二个参数是所有已设好的收件人地址
                        transport.sendMessage(message, message.getAllRecipients());
                    } catch (Exception e) {
                        log.error("发送邮件失败", e);
                    } finally {
                        if (transport != null) {
                            transport.close();
                        }
                    }
                    break;
                } catch (AddressException e) {
                    log.error(e);
                } catch (MessagingException e) {
                    log.error(e);
                }
            }
            log.error("sendMail cost：" + (TimeUtil.currentTimeMillis() - bigen));
        }
    }

    /**
     * 发送邮件比较消耗，开启新线程执行的，会尝试5次发送
     *
     * @param title
     * @param context
     */
    public static void sendMail(String title, String context) {
        //TODO 记得开放代码
//        new Thread(new MialTask(title, context)).start();
    }

    public static void main(String[] args) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("sssssssssssss").append("\n");
        sb.append("sssssssssssss").append("\n");
        sb.append("sssssssssssss").append("\n");
        sb.append("sssssssssssss").append("\n");
        MailUtil.sendMail("测试服务器通知" + TimeUtil.currentTimeMillis(), sb.toString());
    }
}
