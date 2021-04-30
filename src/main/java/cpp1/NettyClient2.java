package cpp1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyClient2 {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .handler(new ProxyServerHandle());


            //不需要建立连接，绑定0端口是表示系统为我们设置端口监听
            Channel channel = b.bind(0).sync().channel();

            //UDP使用DatagramPacket发送数据
            int i = 0;
            while (i < 1000){
                channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("ANSWER - " + i, CharsetUtil.UTF_8),
                        new InetSocketAddress("127.0.0.1", 8081)));
                i++;
                TimeUnit.MILLISECONDS.sleep(10);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
