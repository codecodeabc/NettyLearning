package cpp1;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.net.InetSocketAddress;

public class proxyServer {

    public static void main(String[] params) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // TODO Auto-generated method stub
                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(new ByteArrayEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            private Channel innerCtx;
                            ChannelFuture connectFuture;
                            String msg;
                            @Override
                            public void channelActive(ChannelHandlerContext ctx)
                                    throws Exception {

                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // TODO Auto-generated method stub
                                ctx.channel().remoteAddress();
                                System.out.println();
                                //System.out.println("project链接服务" + ctx.channel().toString());
                                if (connectFuture.isDone()) {
                                    // do something with the data
                                    //channel并不共享，共享的是线程EventLoop，所以如果想向内层转发的话
                                    //需要持有内层的channel
                                    if (innerCtx != null && innerCtx.isActive()) {
                                        innerCtx.writeAndFlush(msg);
                                        innerCtx.flush();
                                    }
                                }
                                ctx.channel().writeAndFlush(msg);
                            }
                        });
                    }
                });

        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080)).sync();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture)
                    throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Server bound 8080");
                } else {
                    System.err.println("Bind attempt failed");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }
}
