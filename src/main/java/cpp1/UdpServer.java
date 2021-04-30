package cpp1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;


public class UdpServer {

    private static int port = 8081;

    public static void main(String[] params) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_RCVBUF, 1024 * 4)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(4096))
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
                        System.out.println("udpserver收到数据：" + msg.content().toString(CharsetUtil.UTF_8));

                        EventLoopGroup group = new NioEventLoopGroup();
                        try {
                            Bootstrap b = new Bootstrap();
                            b.group(group).channel(NioDatagramChannel.class)
                                    .handler(new ProxyServerHandle());

                            //不需要建立连接，绑定0端口是表示系统为我们设置端口监听
                            Channel channel = b.bind(0).sync().channel();

                            //UDP使用DatagramPacket发送数据
                            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(msg.content().toString(CharsetUtil.UTF_8), CharsetUtil.UTF_8),
                                    new InetSocketAddress("10.32.88.178", 3389)));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            group.shutdownGracefully();
                        }
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
