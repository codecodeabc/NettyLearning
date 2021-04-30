package cpp1;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class UdpServerTarget {

    private static int port = 8082;

    public static void main(String[] params) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                        System.out.println("serverTarget收到数据：" + msg.content().toString(CharsetUtil.UTF_8));
                    }
                });


        ChannelFuture future = bootstrap.bind(new InetSocketAddress(port));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("udp Server bound {} success " + port);
            } else {
                System.out.println("udp Server bound {} fail " + port);
                // todo 失败推送
                channelFuture.cause().printStackTrace();
            }
        });
    }
}
